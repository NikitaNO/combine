package code.snippet

/**
 * Created by nikita on 01.03.15.
 */

import code.model.{EmailSubscriber, User}
import net.liftweb.http.SHtml
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JsCmds
import net.liftweb.util.Helpers._

import scala.xml.{NodeSeq, Text}

class LandingSnippet {

  def render(ns: NodeSeq) = {
    var email = ""
    var pass = ""
    var emailSubscriber = ""

    val css = "#use_now_btn [href]" #> MainPage.url &
    "#register-email" #> SHtml.ajaxText("", email = _ , "class" -> "register-input white-input") &
    "#register-pass" #> SHtml.ajaxText("", pass = _ , "type" -> "password", "class" -> "register-input white-input")  &
    "#email_newsletter" #> SHtml.ajaxText("", emailSubscriber = _ , "class" -> "register-input white-input",
      "type"->"email", "style"->"float:center;")  &
    "#submit-button-newsletter" #> SHtml.ajaxButton("Подписаться", () => {
      EmailSubscriber.create.email(emailSubscriber).save()
      JsCmds.Alert("Спасибо за то , что следите за нашими новостями!")
    }, "type" -> "submit")  &
    "#register-button" #> SHtml.ajaxButton(Text("Зарегистрироваться") , () => register(email, pass), "class" -> "register-submit")
    css(ns)
  }

  def register(email: String, pass: String) = {
    val user = User.create.email(email).password(pass)
    val errs = User.validateSignup(user)
    if (errs.isEmpty) {
      user.save()
      User.sendValidationEmail(user)
      JsRaw("alert('Пожалуйста, проверьте свою почту для активации аккаунта!')").cmd & JsCmds.RedirectTo(MainPage.url)
    }
    else JsRaw("alert('Поля заполнены неверно!')").cmd
  }
}

object LandingSnippet {
  val url = "/"
}
