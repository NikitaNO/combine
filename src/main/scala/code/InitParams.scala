package code

import code.parsers.rss.WebLancerHtmlFeed
import code.parsers.rss.feeds._

/**
 * Created by Ni on 18.12.2014.
 */
case class InitParams(val feeds: Seq[Feed], val acc: TwitterAccount)

case class TwitterAccount(val consumerKey: String , val consumerSecret : String, val accessToken : String, val accessTokenSecret: String )

object TestAccount extends TwitterAccount(
  consumerKey = "aQ7onS00toq06klwmugqAbza1",
  consumerSecret = "z0hQ0xSkHQeLdXeOAkRQBlthBOm7E7bo35tjnkVOANwcmAsEpg",
  accessToken = "3012119866-wiiGFvDZbhyqFBzRYDFE9lBgRxtt6pmLhf2ov8X",
  accessTokenSecret = "LjURcFsDVzw9fqUlZiVZAvxfR05LnllHjpg2hnGSKeHTw")


object TestInitParams extends InitParams(Seq(MyPublicationFeed, EtextFeed, AdvegoFeed, WebLancerHtmlFeed) , TestAccount)

object Settings{
  val rssFeeds = Map(
  "weblancer" -> "http://www.weblancer.net/rss/jobs.rss",
  "odesk" -> "https://www.odesk.com/jobs/rss?q=rss",
  "copywriter" -> "http://jobs.designweek.co.uk/jobsrss/?JobFunctions=2006&countrycode=GB",
  "jobsmonster" -> "http://rss.jobsearch.monster.com/rssquery.ashx?q=copywriter&rad_units=miles&brd=1&cy=US&pp=25&sort=rv.di.dt&baseurl=jobview.monster.com"
  )
  val vacancyRssFeeds = Map(
    "simplyhired" -> "http://www.simplyhired.com/a/job-feed/rss/q-freelance+copywriter",
    "jobopenings" -> "http://www.freelancejobopenings.com/copywriting/feeds/rss20"
  )

  object Feed extends Enumeration{
    type Feed = Value
    val advego = Value("Advego")
    val weblancer = Value("Weblancer")
    val etext = Value("Etext")
    val myPublication = Value("My Publication")
    val general = Value("General")
  }

  object FeedType extends  Enumeration{
    type FeedType = Value
    val html, rss = Value
  }
}
