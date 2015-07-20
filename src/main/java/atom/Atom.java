//package atom;
//
//import org.apache.abdera.Abdera;
//import org.apache.abdera.model.Document;
//import org.apache.abdera.model.Entry;
//import org.apache.abdera.model.Feed;
//import org.apache.abdera.parser.Parser;
////import org.apache.abdera.parser.stax;
////import org.apache.axiom.om.util;
////import org.apache.axiom.om.impl.llom.factory;
//
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.List;
//
///**
// * Created by Ni on 05.12.2014.
// */
//public class Atom {
//
//    public static List<Entry> getEntries(String feedUrl) {
//        Abdera abdera = new Abdera();
//        Parser parser = abdera.getParser();
//        URL url = null;
//        try {
//            url = new URL(feedUrl);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        Document<Feed> doc = null;
//        try {
//            doc = parser.parse(url.openStream(), url.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Feed feed = doc.getRoot();
//        return feed.getEntries();
//    }
//}
