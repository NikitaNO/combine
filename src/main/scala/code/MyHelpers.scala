package code

import java.io.File
import java.net.URL
import java.text.{DecimalFormat, SimpleDateFormat}
import java.util.Date

import code.model.{Rate, CurrencyType, PostModel, User}
import net.liftweb.common._
import net.liftweb.http.{SessionVar, Templates}
import net.liftweb.json.JsonAST.{JString, JValue}
import net.liftweb.util.Helpers._

import scala.xml.NodeSeq


object MyHelpers extends Loggable {
  object curUser extends  SessionVar[Box[User]](User.currentUser)
  val dateFormat = new SimpleDateFormat("dd-MM-yyyy")
  val decimalFormat = new DecimalFormat("###.##")
  var allPosts = PostModel.findAll()


  implicit def logException(t: Throwable) = logger.error(t)

  implicit def path2nodeSeq(path: List[String]) : NodeSeq = Templates(path) openOrThrowException "Mail template is undefined!"

  // pattern pimp-my-library
  implicit class InsensetiveString(total: String) {
    implicit def caseContains(sub: String) = total.toUpperCase.contains(sub.toUpperCase)
  }

  implicit class JObj(a: Any) {
    implicit def string2Jstring(s: String): JValue = JString(s)
  }

  implicit class Budget(budg: String) {
    implicit def toBudget = {
      tryo {
        val usd = "у.е."
        val budgStr = if (budg.contains(usd)) budg.replaceAll(usd, "") else budg
        budgStr.replaceAll("[^0-9.]", "").toFloat
      } openOr 0f
    }
  }

  implicit class CustomDate(s: String) {
    implicit def toFormatDate = dateFormat.parse(s)
  }

}