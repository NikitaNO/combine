package code.rest

import bootstrap.liftweb.{ParseSession, MainActor}
import code.Filters._
import code.MyHelpers._
import code.Settings.Feed
import code.model.PostModel
import net.liftweb.http.S
import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import net.liftweb.mapper.By
import net.liftweb.util.Helpers._
import org.joda.time.format.DateTimeFormat


/**
 * Created by nikita on 29.12.14.
 */
 // enable / disable some rows to provide needed functionality. now used only for test purposes
object JobsRest extends RestHelper  {
  serve {
    case JsonGet("service" :: "jobs2" :: _, _) => {
      val feeds = S.param("feed").map(_.split(","))
      val posts = feeds.map(_.flatMap {
        feed =>
          val f = tryo(logException(_))(Feed.withName(feed)) openOr Feed.general
          PostModel.findAll(By(PostModel.feed, f))
      }.toList).openOr(PostModel.findAll())
      val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
      val fromDate = S.param("fromDate").map(formatter.parseDateTime(_).toDate)
      val untilDate = S.param("untilDate").map(formatter.parseDateTime(_).toDate)
      val fromBudg = S.param("fromBudget").map(_.toFloat)
      val untilBudg = S.param("untilBudget").map(_.toFloat)
      val fromPost = S.param("fromPost").map(_.toInt).openOr(0)
      val untilPost = S.param("untilPost").map(_.toInt).openOr(Int.MaxValue)
      val total = S.param("total").map(_.toInt).openOr(Int.MaxValue)
      val text = S.param("text")
      //todo remake
      //      val posts = GetJobsService.getJobs(TestInitParams.feeds, Full(DateTime.now.minusMonths(5))).toList
      val filtered = PostModel.findAll(/*
        By_>(PostModel.date, fromDate.openOr(DateTime.now.minusMonths(6).toDate)),
        By_<(PostModel.date, untilDate.openOr(DateTime.now.plusMonths(6).toDate))*/).toList
      val sliced = filtered.take(total).slice(fromPost, untilPost)
        .byText(text) //.byBudget(fromBudg,untilBudg)//posts.byDate(fromDate,untilDate).byText(text).byBudget(fromBudg,untilBudg)
      val jposts = sliced.map(p =>
          ("title" -> p.title.get) ~
            ("description" -> p.description.get) ~
            ("budget" -> p.budget.get) ~
            ("date" -> Option(p.date.get).map(_.toString).getOrElse("N/A")) ~
            ("link" -> p.link.get) ~
            ("feed" -> p.feed.get.toString) ~
            ("feed" -> p.rubAmount.get.toString) ~
            ("feed" -> p.usdAmount.get.toString)
        )
      JObject(List(JField("total", jposts.size), JField("posts", jposts: JArray)))
    }

    case JsonGet("service" :: "jobsTotal" :: _, _) => {
      JObject(List(JField("total", PostModel.findAll.size)))
    }

    case JsonGet("service" :: "cleanDb" :: _, _) => {
      ParseSession.postsBot.testDbClean()
      JObject(List(JField("total before", PostModel.findAll.size)))
    }
  }
}


