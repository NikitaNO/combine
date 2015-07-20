package bootstrap.liftweb

import code.Settings.FeedType
import code.model.{CurrencyType, PostModel}
import code.parsers.rss._
import code.parsers.rss.feeds.{Feed, RssFeed}
import code._
import net.liftweb.actor.LiftActor
import net.liftweb.common.{Loggable, Box, Empty, Full}
import net.liftweb.mapper.By_<
import net.liftweb.util.Schedule
import org.joda.time.DateTime
import twitter4j.TwitterException
import net.liftweb.util.Helpers._

object MainActor{
  var iterations = 0
}

class MainActor(val initParams: InitParams) extends LiftActor with Loggable  {
  val twitterAcc = initParams.acc
  val twitterBot: Box[TwitterBot] = Full(TwitterBot(twitterAcc))
  def nextCurrencyReset = DateTime.now().plusDays(1).withHourOfDay(5)
  def nextDbClean = DateTime.now().plusDays(1).withHourOfDay(4)

  override protected def messageHandler: PartialFunction[Any, Unit] = {
    case GetJobsMsg(date) =>
      logger.info("Getting posts is initialized")
      twitterBot.foreach(tb => {GetJobsService.getJobs(initParams.feeds)})
      MyHelpers.allPosts = PostModel.findAll()
      initNextGetJobs(date.plusMinutes(4))
    case CurrencyMsg(date) =>
      logger.info("Currency resetting was called")
      PostModel.findAll().foreach { post =>
        tryo/*(logException _)*/ {
          val num = post.budget.get
            .replaceAll("\\D", "").toDouble
          val (rAmount,uAmount): (Double,Double) = post.originalCurrency.get match {
            case CurrencyType.rub =>
              val converted = CurrencyHelpers.convert(post.originalCurrency, CurrencyType.usd, num)
              (num, converted)
            case CurrencyType.usd =>
              val converted = CurrencyHelpers.convert(post.originalCurrency, CurrencyType.rub, num)
              (converted, num)
          }
          post.rubAmount(rAmount).usdAmount(uAmount).save()
        }
      }
      initCurrencyReset(nextCurrencyReset)
    case CleanMsg(date) =>
      logger.info("Cleaning db was started")
      PostModel
        .findAll(By_<(PostModel.date, DateTime.now().minusDays(3).toDate))
        .foreach(_.delete_!)
      logger.info("There are " + PostModel.findAll().size + " posts after cleaning.")
      initNextDbClean(nextDbClean)
  }

  private def delay(ld: DateTime) = ld.toDate.getTime - DateTime.now.toDate.getTime


  def   initScripts(): Unit = {
    val firstGetJobs = DateTime.now().plusMillis(1000)
    initNextGetJobs(firstGetJobs)
    initCurrencyReset(nextCurrencyReset)
    initNextDbClean(nextDbClean)
  }

  def initCurrencyReset(nextDate: DateTime): Unit = {
    Schedule.schedule(this, CurrencyMsg(nextDate), delay(nextDate))
  }

  private def initNextGetJobs(nextDate: DateTime, feeds: RssFeed*): Unit = {
    Schedule.schedule(this, GetJobsMsg(nextDate), delay(nextDate))
  }

  private def initNextDbClean(nextDate: DateTime): Unit = {
    logger.info("Schedule cleaning db in " + nextDate)
    Schedule.schedule(this, CleanMsg(nextDate), delay(nextDate))
  }

  def testDbClean(): Unit = {
    initNextDbClean(DateTime.now().plusSeconds(30))
    logger.info("Cleaning will start after 30 sec")
  }

  def postJobs(jobs: Seq[PostModel], bot: TwitterBot) {
    for (job <- jobs) {
      try {
        bot.postTweet(job.toTweet)
        job.published(true).save
        logger.info(s"SUCCESSFULLY POSTED: ${job.title} at ${DateTime.now()}")
      }
      catch {
        case e: TwitterException => logger.error(e)
        case e: Exception => logger.error(e)
      }
    }
  }

  case class GetJobsMsg(d: DateTime)
  case class CurrencyMsg(d : DateTime)
  case class CleanMsg(d : DateTime)

}


object GetJobsService extends Loggable {
  def getJobs(feeds: Seq[Feed], date: Box[DateTime] = Empty) = {
    MainActor.iterations += 1
    logger.info(s"Iteration ${MainActor.iterations} : Getting jobs from feeds $feeds...")
    val jobs = (for (feed <- feeds) yield {
      logger.info(s"$feed ...")
      tryo((e: Throwable) => logger.error(e)) {
        val jobs = feed.feedType match{
          case FeedType.html => WebLancerHtmlFeed.posts()
          case FeedType.rss => RssParser.proccessFeed(feed)
        }
        jobs
      }openOr Nil
    }).flatten
    jobs
  }

  def wrongFeedsWork(feeds: Seq[Feed], date: Box[DateTime] = Empty) = {
    logger.info(s"Testing : Getting jobs from feeds $feeds...")
    val jobs = (for (feed <- feeds) yield {
      logger.info(s"$feed ...")
      tryo( (_: Throwable) => EmailActions.sendErrorMail(feed)) {
        val jobs = feed.feedType match{
          case FeedType.html => WebLancerHtmlFeed.posts(save = false)
          case FeedType.rss => RssParser.proccessFeed(feed, test = true)
        }
        val smthWrong = jobs.isEmpty || jobs
          .filterNot(j => j.budget.isEmpty || j.budget == "N/A").isEmpty ||
          jobs.filterNot(_.date == null).isEmpty
        if(smthWrong) EmailActions.sendErrorMail(feed)
        !smthWrong
      } openOr false
    })
    jobs.contains(false)
  }
}