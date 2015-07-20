package code.parsers.rss.feeds

import java.io.{ByteArrayInputStream, InputStream}
import java.net.URL
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat

import code.Settings.{Feed, FeedType}
import code.model.{CurrencyType, PostModel}
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.io.{SyndFeedInput, XmlReader}
import net.liftweb.common.{Loggable, Full}
import net.liftweb.util.Helpers._
import org.jsoup.Jsoup

import scala.collection.JavaConversions._
import scala.collection.immutable.StringOps
import scala.xml.XML

/**
 * Created by Ni on 07.12.2014.
 */
// todo think of realizing type-class pattern
trait Feed extends Loggable {
  val feed : Feed.Value
  val feedType : FeedType.Value
  val url : String
  
  def posts(save: Boolean = true) : List[PostModel]
}

trait RssFeed extends Feed  {
  val feedType = FeedType.rss

  def entries = {
    val input = new SyndFeedInput()
    val feed = input.build(new XmlReader(inputStream))
    val entries = feed.getEntries.toList
    logger.info(s"Got ${entries.size} items")
    entries
  }

  def entry2Post(e : SyndEntry, save:Boolean = true): PostModel

  def entries2Posts(e : List[SyndEntry], save: Boolean = true): List[PostModel]
  
  def inputStream: InputStream
  
  def posts(save: Boolean = true) = entries2Posts(entries, save)
}

 class StandartRssFeed(url_ : String) extends  RssFeed{
   val feed = Feed.general
   val url = url_

  override def inputStream: InputStream = new URL(url).openStream()

  override def entry2Post(e : SyndEntry , save:Boolean = true): PostModel = {
    val title = Jsoup.parse(e.getTitle).text()
    val descr = Jsoup.parse(e.getDescription.getValue).text()
    PostModel(title,descr,e.getLink,Full(e.getPublishedDate), "N/A", feed,CurrencyType.usd, save = save)
  }

  override def entries2Posts(e: List[SyndEntry], save: Boolean = true) = e.map(entry2Post(_, save))
}

object EtextFeed extends StandartRssFeed("http://www.etxt.ru/rss/tasks/") {
  override val feed = Feed.etext

  override def entries2Posts(e: List[SyndEntry], save: Boolean = true) = {
    logger.info(s"Processing ${e.size} posts")
    super.entries2Posts(e, save)
  }

  override def entry2Post(e: SyndEntry, save:Boolean = true): PostModel = {
    val descrTable = Jsoup.parse(e.getDescription.getValue).toString
    val xml = tryo{
      val nsStr = descrTable
        .replaceAll("<br>","")
        .replaceAll("&nbsp","")
        .replaceAll("nowrap","")
        .split("<body>").last.split("</body>").head  //TODO remake with regex, it's just workaround
      val xml = XML.loadString(nsStr)
      nsStr
    } openOr ""
    val post = super.entry2Post(e, save)
    if(save) post.descriptionNodeSeq(xml).originalCurrency(CurrencyType.rub).save
    post
  }
}

object MyPublicationFeed extends  StandartRssFeed("http://www.my-publication.ru/rss/projects.xml")
{
  override val feed = Feed.myPublication

  override def entries2Posts(e: List[SyndEntry], save: Boolean = true) = {
    logger.info(s"Processing ${e.size} posts")
    super.entries2Posts(e)
  }

  override def entry2Post(e: SyndEntry, save:Boolean = true): PostModel = {
    val title = Jsoup.parse(e.getTitle).text()
    val lastBrackets = (title.lastIndexOf("(") + 1, title.lastIndexOf(")"))
    val budg = tryo(title.substring(lastBrackets._1, lastBrackets._2)).openOr("")
    val cur = if(budg.contains("Р.")) CurrencyType.rub else CurrencyType.usd
    val post = super.entry2Post(e, save)
    if(save) post.budget(budg).originalCurrency(cur).save()
    post
  }
}

trait AdvegoFeedGeneral extends RssFeed {
  val feed = Feed.advego
  val url = "http://advego.ru/rss/orders/new.rss"
  private val advegoDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

  def title2Date= {
    val xml = XML.load(new URL(url))
    val xmlEntries = xml \\ "entry"
    ((xmlEntries \\ "title").map(_.text) zip (xmlEntries \\ "published").map(_.text)).toMap
  }

  override def entry2Post(e: SyndEntry, save:Boolean = true): PostModel = {
    val titleString = e.getTitle.replaceAll("\\n","")
    val params = titleString.split("/")
    val title = params.last
    val budg = params.map(_.trim).head
    val descr =
      s"Количество знаков : ${params(1)} " +
      s"Срок исполнения : ${params(2)} " +
      s"Категория : ${params(3)} " +
      s"Тип : ${params(4)},${params(5)} "
    val date = advegoDateFormat.parse(title2Date(e.getTitle))
    logger.info(budg)
    PostModel(title, descr , e.getLink, Full(date) , budg, Feed.advego,CurrencyType.usd, save = save)
  }

  override def entries2Posts(e: List[SyndEntry], save: Boolean = true): List[PostModel] = {
    logger.info(s"Trying to add ${e.size} posts")
    e.map(entry2Post(_, save))
  }

  override def inputStream = {
    val xml = XML.load(new URL(url))
    val reformedXml = xml.toString().replace("published","pubDate")
    new ByteArrayInputStream(reformedXml.getBytes(StandardCharsets.UTF_8))
  }
}

object AdvegoFeed extends AdvegoFeedGeneral

class WebLancerFeedGeneral extends StandartRssFeed("http://www.weblancer.net/rss/jobs.rss"){
  override val feed = Feed.weblancer

  override def entry2Post(e : SyndEntry, save: Boolean = true) = {
    val title = new StringOps(e.getTitle).capitalize
    val descr = new StringOps(e.getDescription.getValue).capitalize
    val pattern = "class=\"amount_2\">(.*?)</b>".r
    val budg = pattern.findFirstIn(descr).map(s => s.substring(s.indexOf(">") + 1,s.indexOf("<")))
    val newDescription = descr.replaceAll("<[^>]*>", " ")
    PostModel(title,
    newDescription,
    e.getLink,
    Full(e.getPublishedDate),
    budg.getOrElse(""),
    Feed.weblancer,
    CurrencyType.usd,
    save = save)
  }
}

object WebLancerFeed extends WebLancerFeedGeneral
