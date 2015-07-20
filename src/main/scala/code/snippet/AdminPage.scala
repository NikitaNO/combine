package code.snippet

import bootstrap.liftweb.GetJobsService
import code.Settings.Feed
import code.TestInitParams
import net.liftweb.http.js.{JsCmd, JsCmds}
import net.liftweb.http.{SHtml, SessionVar}

import scala.xml.Text

class AdminPage {
  object Feeds extends SessionVar[Map[Feed.Value, Boolean]](TestInitParams.feeds.map(_.feed -> true).toMap)

  def feeds = {
     TestInitParams.feeds
       .map {
       feed =>
         val checkbox = SHtml.ajaxCheckbox(Feeds.is(feed.feed), b => {
           Feeds.set(Feeds.is ++ Map(feed.feed -> b))
           JsCmds.Noop
         }, "class" -> "check")
         <label>
           {checkbox}{feed.feed.toString}
         </label>}
   }

  def testBtn = SHtml.ajaxButton(Text("Test Feeds"), () => testFeeds())

  def testFeeds(): JsCmd = {
    val feeds = TestInitParams.feeds.filter(f => Feeds.is.contains(f.feed))
    if(GetJobsService.wrongFeedsWork(feeds))
      JsCmds.Alert("Something is wrong! Check admin's email for more information")
    else
      JsCmds.Alert("All is OK")
}
}
