package code.parsers.rss

import java.text.SimpleDateFormat
import java.util.Date

import code.MyHelpers._
import code.Settings.{Feed, FeedType}
import code.model.{CurrencyType, PostModel}
import code.parsers.rss.feeds.Feed
import net.liftweb.common.{Box, Empty, Full}
import net.liftweb.util.Helpers._
import org.joda.time.DateTime
import org.jsoup.Jsoup
import org.jsoup.select.Elements

import scala.collection.JavaConversions._

/**
 * Created by nikita on 24.01.15.
 */
trait Parser{
  def proccessFeed(hf: Feed, test: Boolean = false): List[PostModel]
}

trait HtmlParser {
  def proccessFeed(hf: HtmlFeed): List[PostModel]
}

trait HtmlFeed extends Feed{
  val listUrl: String
  val feedType = FeedType.html

  def getPosts(save: Boolean = true): List[Box[PostModel]]

  def posts(save: Boolean = true) = getPosts(save).filter(_.isDefined).map(_.openOrThrowException("Something unexpected!"))
}

object WebLancerHtmlFeed extends HtmlFeed  {
  val feed = Feed.weblancer
  val url = listUrl
  override val listUrl: String = "https://www.weblancer.net/projects/?category_id=5"
  val baseUrl = "https://www.weblancer.net"

  implicit def elements2List(els: Elements) = els.iterator().toList

  override def getPosts(save : Boolean): List[Box[PostModel]] = {
    logger.info("Processing Weblancer:")
    val posts = for (i <- 1 to 5) yield {
      logger.info(s"page $i")
      val url = listUrl + "&page=" + i
      val doc = Jsoup.connect(url).get()
      doc
        .getElementsByClass("items_list")(0)
        .getElementsByTag("tr").tail.map { el =>
        tryo(logException(_)){
          val items = el.getElementsByClass("item")
          logger.info(s"Posts on page : ${items.size()}")
          val (title, link) = items.map(it => (it.text, baseUrl + it.attr("href"))).get(0)
          val (category, dateStr) = {
            val infos = el.getElementsByClass("il_item_descr").text().split('|')
            (infos(0), infos(1))
          }
          val List(budg, respStr) = el.getElementsByClass("il_medium").iterator().toList.take(2).map(_.text)
          val description = descrFromDetails(link)
          val date = {
            if (dateStr.contains("сегодня")) Full(new Date)
            else
            if (dateStr.contains("вчера")) Full(DateTime.now().minusDays(1).toDate)
            else {
              val format = new SimpleDateFormat("dd.MM.yyyy в hh:mm")
              Full(format.parse(dateStr))
            }
          }
          val responses = tryo(respStr.split("(").head.toInt) openOr 0
          Full(PostModel(title, description, link, date, budg, Feed.weblancer,CurrencyType.usd,responses, save = save))
        } openOr Empty
      }
    }
     logger.info(s"total posts : ${posts.flatten.size}")
     posts.flatten.toList
  }


  def descrFromDetails(projLink: String) = {
    val doc = Jsoup.connect(projLink).get()
    doc.getElementsByClass("id_description").text().replaceAll("-","")
  }

}

object Test extends App{
  val l = WebLancerHtmlFeed.getPosts()
  l.foreach(println(_))
}

case class HtmlEntry(val title: String, val link: String, val budg: String, val date: Date)