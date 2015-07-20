package code

import java.io.IOException
import java.util.Date

import code.MyHelpers._
import code.Settings.Feed
import code.model.{CurrencyType, PostModel}
import net.liftweb.common.Box
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.{TextField, Field, Document}
import org.apache.lucene.index.{DirectoryReader, IndexWriter, IndexWriterConfig}
import org.apache.lucene.queryparser.classic.{QueryParser, ParseException}
import org.apache.lucene.search.{ScoreDoc, IndexSearcher}
import org.apache.lucene.store.RAMDirectory

/**
 * Created by nikita on 29.12.14.
 */
object Filters {

  implicit class filteredList(l: List[PostModel]) {

    def fromDate(from: Box[Date]): List[PostModel] = {
      from.map(date => l.filter(_.date.get != null).filter(post => date.before(post.date))).openOr(l)
    }

    def untilDate(until: Box[Date]): List[PostModel] = {
      until.map(date => l.filter(_.date.get != null).filter(post => date.after(post.date))).openOr(l)
    }

    def byText(text: Box[String]): List[PostModel] =
      text.map(text => {
        FuzzySearch.search(text, l)
      }).openOr(l)

    def fromBudget(from: Box[Float], currency : CurrencyType.Value): List[PostModel] =
      from.map(budg =>
        currency match {
          case CurrencyType.rub =>
            l.filter(_.rubAmount.get > budg)
              case CurrencyType.usd =>
                l.filter(_.usdAmount.get > budg)
            }).openOr(l)

    def untilBudget(until: Box[Float], currency : CurrencyType.Value): List[PostModel] =
      until.map(budg =>
        currency match {
          case CurrencyType.rub =>
            l.filter(_.rubAmount.get < budg)
          case CurrencyType.usd =>
            l.filter(_.usdAmount.get < budg)
        }).openOr(l)


    def byFeeds(feeds: Set[Feed.Value]) = l.filter(pm => feeds.contains(pm.feed.get))

    def filterPosts(from: Box[Date], until: Box[Date],
                    text: Box[String], fromBudg: Box[Float],
                    untilBudg: Box[Float], currency: CurrencyType.Value, feeds: Set[Feed.Value]) =
      l.fromDate(from).untilDate(until)
      .byText(text).fromBudget(fromBudg,currency).untilBudget(untilBudg,currency).byFeeds(feeds)
  }
}


object FuzzySearch {
  private val SIMILARITY: String = "~0.7"
  private val FIELD: String = "content"

  @throws(classOf[IOException])
  @throws(classOf[ParseException])
  def search(text: String, posts: List[PostModel]) = {
    val post2Str = posts.flatMap(p => List(p.title.get -> p , p.description.get -> p)).toMap
    val strings = post2Str.keys.toList
    val matches: List[String] = Nil
    val analyzer = new StandardAnalyzer
    val directory = new RAMDirectory
    val config = new IndexWriterConfig(analyzer)
    val iwriter = new IndexWriter(directory, config)
    for (string <- strings) {
      val document: Document = new Document
      document.add(new Field(FIELD, string, TextField.TYPE_STORED))
      iwriter.addDocument(document)
    }
    iwriter.commit
    val ireader = DirectoryReader.open(directory)
    val isearcher = new IndexSearcher(ireader)
    val queryParser = new QueryParser(FIELD, analyzer)
    val query = queryParser.parse(text + SIMILARITY)
    val hits: Array[ScoreDoc] = isearcher.search(query, null, strings.size).scoreDocs
    val docs = (for (hit <- hits) yield {
      isearcher.doc(hit.doc).get(FIELD)
    }).toList
    docs.map(post2Str(_)).toList
  }
}