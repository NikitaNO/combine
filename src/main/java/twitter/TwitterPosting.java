package twitter;

import twitter4j.Logger;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

import static twitter4j.TwitterFactory.getSingleton;

/**
 * Created by Ni on 07.11.2014.
 */

/*Currency updater:
        Consumer Key (API Key)	Dj0RtQH0oltlLANafwN3t2Whe
        Consumer Secret (API Secret)	ac9JYvSGTz7BpNgwGOUOOgOo8893HX7PrAKG8ErrIGPPrPvJkt
        Access Token	2848266965-EvAvfKopKBtmryU1BrlinyaqnvHkg9r7k34hWzt
        Access Token Secret	Ry2bzElErIWZWaZMbCBlBIKMFSkFMdp9H5XVXBcyniTEJ*/

//public class TwitterPosting {
//    static Twitter twitter = getSingleton();
//    static {
//        String consumerKey = "Dj0RtQH0oltlLANafwN3t2Whe";
//        String consumerSecret = "ac9JYvSGTz7BpNgwGOUOOgOo8893HX7PrAKG8ErrIGPPrPvJkt";
//        twitter.setOAuthConsumer(consumerKey, consumerSecret);
//    }
//    private static Logger LOG = Logger.getLogger(TwitterPosting.class);
//
//   static public void postTweet(String msg) throws TwitterException {
//        String accessToken = "2848266965-EvAvfKopKBtmryU1BrlinyaqnvHkg9r7k34hWzt";
//        String accessTokenSecret = "Ry2bzElErIWZWaZMbCBlBIKMFSkFMdp9H5XVXBcyniTEJ";
//
//        twitter.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret));
////        String message = "\"A Visit to Transylvania\" by Euromaxx: Lifestyle Europe (DW) \n http://bit.ly/1cHB7MH";
//        Status status = twitter.updateStatus(msg);
//        LOG.debug("Successfully updated status to " + status.getText());
//    }
//
//}