//package code
//
//import java.io.IOException
//
//import org.apache.lucene.analysis.standard.StandardAnalyzer
//import org.apache.lucene.document.{Document, Field, TextField}
//import org.apache.lucene.index.{DirectoryReader, IndexWriter, IndexWriterConfig}
//import org.apache.lucene.queryparser.classic.{ParseException, QueryParser}
//import org.apache.lucene.search.{IndexSearcher, ScoreDoc}
//import org.apache.lucene.store.RAMDirectory
//
//
///**
// * Created by nikita on 16.04.15.
// */
//object FuzzySearch {
//  private val SIMILARITY = "~0.85"
//  private val FIELD = "content"
//  private var analyzer = new StandardAnalyzer
//  private var directory = new RAMDirectory
//  private var config = new IndexWriterConfig(analyzer)
//
//  @throws(classOf[IOException])
//  def addStrings(strings: List[String]) {
//    val iwriter = new IndexWriter(directory, config)
//    for (string <- strings) {
//      val document = new Document
//      document.add(new Field(FIELD, string, TextField.TYPE_STORED))
//      iwriter.addDocument(document)
//    }
//    iwriter.commit
//  }
//
//  @throws(classOf[IOException])
//  @throws(classOf[ParseException])
//  def search(text: String, strings: List[String]): List[String] = {
//    val ireader = DirectoryReader.open(directory)
//    val isearcher = new IndexSearcher(ireader)
//    val queryParser = new QueryParser(FIELD, analyzer)
//    val query = queryParser.parse(text + SIMILARITY)
//    val hits: Array[ScoreDoc] = isearcher.search(query, null, strings.size).scoreDocs
//    (for (hit <- hits) yield isearcher.doc(hit.doc).get(FIELD)).toList
//  }
//}
