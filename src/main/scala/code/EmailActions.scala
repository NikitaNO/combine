package code

import code.parsers.rss.feeds.Feed
import net.liftweb.util.Mailer.{From, PlainMailBodyType, Subject, To}
import net.liftweb.util.{Mailer, Props}

object EmailActions {
  def sendErrorMail(feed: Feed) = {
    val from = Props.get("mail.user") openOr ""
    val to = Props.get("mail.admin") openOr ""
    val subj = s"Something wrong with feed ${feed.feed.toString}"
    val body = s"Please check feed ${feed.feed.toString} , because all the posts service received has empty date or empty budget."
    Mailer.sendMail(From(from), Subject(subj),To(to), PlainMailBodyType(body))
  }
}
