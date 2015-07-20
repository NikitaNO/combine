import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import org.apache.lucene.queryparser.classic.ParseException;
import java.util.ArrayList;

public class FuzzySearch {
    private static final String SIMILARITY = "~0.85";
    private static final String FIELD = "content";

    private static Analyzer analyzer = new StandardAnalyzer();
    private static Directory directory = new RAMDirectory();
    private static IndexWriterConfig config = new IndexWriterConfig(analyzer);

    public static void addStrings(ArrayList<String> strings) throws IOException {
        IndexWriter iwriter = new IndexWriter(directory, config);
        for(String string : strings) {
            Document document = new Document();
            document.add(new Field(FIELD, string, TextField.TYPE_STORED));
            iwriter.addDocument(document);
        }
        iwriter.commit();
    }

    public static ArrayList<String> search(String text, ArrayList<String> strings)
            throws IOException, ParseException {
        ArrayList<String> matches = new ArrayList<>();

        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);
        QueryParser queryParser = new QueryParser(FIELD, analyzer);
        Query query = queryParser.parse(text + SIMILARITY);
        ScoreDoc []hits = isearcher.search(query, null, strings.size()).scoreDocs;

        for(ScoreDoc hit : hits) {
            Document hitDoc = isearcher.doc(hit.doc);
            matches.add(hitDoc.get(FIELD));
        }

        return matches;
    }
}
