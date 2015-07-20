package code.parsers.rss.feeds

import com.rometools.rome.feed.synd.SyndEntry

/**
 * Created by Ni on 20.12.2014.
 */
trait MicroFeed extends RssFeed{
    override def entries = filteredEntries(super.entries)

    def filteredEntries(entries : List[SyndEntry]) : List[SyndEntry]
}

case class AdvegoMicrofeed(val key: String) extends AdvegoFeedGeneral with MicroFeed{
  def filteredEntries(entries : List[SyndEntry]) = entries.filter(_.getTitle.contains(key))
}

case class WebLancerMicrofeed(val category: String) extends WebLancerFeedGeneral with MicroFeed{
  def categoryFromUrl(descr: String) = {
    val pattern = "category_id="
    val catStart= descr.indexOf(pattern) + pattern.length
    val catEnd = descr.substring(catStart,descr.length).indexOf("\"")
    descr.substring(catStart,descr.length).substring(0,catEnd)
  }

  override def filteredEntries(entries: List[SyndEntry]): List[SyndEntry] = entries.filter( e => categoryFromUrl(e.getDescription.getValue) == category)
}