import org.junit.Test;
import twitter4j.*;
import twitter4j.auth.AccessToken;


import static twitter4j.TwitterFactory.*;

/**
* Created by Ni on 07.11.2014.
*/
public class TestTwitterPosting {

    private static Logger LOG = Logger.getLogger(TestTwitterPosting.class);

    @Test
    public void testPostingToTwitter() throws TwitterException {
        String consumerKey = "Dj0RtQH0oltlLANafwN3t2Whe";
        String consumerSecret = "ac9JYvSGTz7BpNgwGOUOOgOo8893HX7PrAKG8ErrIGPPrPvJkt";
        String accessToken = "2848266965-EvAvfKopKBtmryU1BrlinyaqnvHkg9r7k34hWzt";
        String accessTokenSecret = "Ry2bzElErIWZWaZMbCBlBIKMFSkFMdp9H5XVXBcyniTEJ";
        Twitter twitter = getSingleton();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        twitter.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret));
        String message="\"A Visit to Transylvania\" by Euromaxx: Lifestyle Europe (DW) \n http://bit.ly/1cHB7MH";
        Status status = twitter.updateStatus(message);
        LOG.debug("Successfully updated status to " + status.getText());
    }

}
