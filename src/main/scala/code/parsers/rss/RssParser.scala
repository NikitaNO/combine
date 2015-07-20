package code.parsers.rss

import code.model.PostModel
import code.parsers.rss.feeds.Feed

object RssParser extends Parser {

  override def proccessFeed(hf: Feed, test: Boolean = false): List[PostModel] = hf.posts(!test)
}

