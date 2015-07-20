package code

import code.MyHelpers._
import net.liftweb.common.Loggable
import net.liftweb.util.Helpers._
import twitter4j.Status
import twitter4j.TwitterFactory._
import twitter4j.auth.AccessToken

/**
 * Created by Ni on 20.12.2014.
 */
/**
 * Created by Ni on 07.11.2014.
 */
object TwitterBot {
  def apply(acc: TwitterAccount) = new TwitterBot(acc)
}

class TwitterBot(acc: TwitterAccount) extends Loggable {

  private val twitter = getSingleton

  tryo(logException(_)) (twitter.setOAuthAccessToken(new AccessToken(acc.accessToken, acc.accessTokenSecret)))

  def postTweet(msg: String) {
    val status: Status = twitter.updateStatus(msg)
    logger.debug("Successfully updated status to " + status.getText)
  }
}

