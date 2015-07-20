package code.snippet

import code.MyHelpers._
import code.model.User
import code.snippet.MainPage._
import net.liftweb.common.{Loggable, Logger, Empty, Full}
import net.liftweb.http.SHtml
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JsCmds
import net.liftweb.mapper.By
import net.liftweb.util.FieldError
import net.liftweb.util.Helpers._

import scala.xml.NodeSeq

object Modal extends Loggable {
  val template = "register-dialog" :: Nil
  val passwordRegex = "^(?=.*[a-zA-Z].*)(?=.*[0-9].*)([a-zA-Z0-9]{8,12})$"      //letter-numeric pass from 8 to 12 characters

  def checkPasswords(pass: String, confirm: String) =
    if (pass != confirm) List(FieldError(User.password, "Passwords must match")) else Nil

  def showErrors(errors: List[FieldError]) = {
    val checkedFields = List(User.email, User.password)
    checkedFields.map { field =>
      val invalid = errors.find(_.field.uniqueFieldId == field.uniqueFieldId)
      val fieldId = field.uniqueFieldId.openOr("") + "-error"
      val msg = if (invalid.isDefined) {
        invalid.get.msg
      }
      else ""
      JsRaw(s"$$('.$fieldId').text('$msg')").cmd
    }
  }

  def register(email: String, pass: String, confirm: String) = {
    val user = User.create.email(email).password(pass)
    val errs = User.validateSignup(user) ::: checkPasswords(pass, confirm)
    if (errs.isEmpty) {
      user.save()
      User.sendValidationEmail(user)
      JsRaw("alert('Please check your email to activate account!')").cmd &
        JsRaw(s"$$('#registr').modal('hide')").cmd
    }
    else {
      logger.error(errs.mkString(","))
      showErrors(errs).foldLeft(JsCmds.Noop)(_ & _)
    }
  }


  def registerForm(ns: NodeSeq) = {
    var email = ""
    var pass = ""
    var passConf = ""

    def checkPassword(pass: String, fieldId: String) = {
      if(pass.matches(passwordRegex))
        cleanError(fieldId)
      else JsRaw(s"$$('#$fieldId').text('Password should be minimum 8 characters long and contain at least one number!')").cmd
    }

    def checkConfPassword(conf: String, fieldId : String) =
      if(pass == conf) cleanError(fieldId) else JsRaw(s"$$('#$fieldId').text('Passwords must match!')").cmd

    val cssRegister = "#email" #> SHtml.ajaxText("", s => {
      email = s
      checkMail(s, "mod-reg-email")
    }) &
      "#password" #> SHtml.ajaxText("", { s =>
        pass = s
        checkPassword(s, "mod-reg-pass") &
        checkConfPassword(passConf, "mod-reg-conf")
      }, "type" -> "password") &
      "#confirm" #> SHtml.ajaxText("", s => {
        passConf = s
        checkPassword(pass, "mod-reg-pass") &
        checkConfPassword(s, "mod-reg-conf")
      }, "type" -> "password") &
      "#mod-btn-register" #> SHtml.ajaxButton("Зарегистрироваться", () => {
        logger.info("password:" + pass)
        register(email, pass, passConf)
      })
    cssRegister(ns)
  }

  def cleanError(id: String) = JsRaw(s"$$('#$id').text('')").cmd
    

  def checkMail(mail : String, fieldId : String) = {
    if("""(?=[^\s]+)(?=(\w+)@([\w\.]+))""".r.findFirstIn(mail) == None)
      JsRaw(s"$$('#$fieldId').text('Incorrect email!')").cmd
    else cleanError(fieldId)
  }

  def login(email: String, password: String) = {
    val user = User.find(By(User.email, email))
    user match {
      case Full(user) =>
        if (user.password.match_?(password)) {
          User.logUserIn(user)
          RedrawHeader.is.get.setHtml() &
            RedrawPosts.is.get.setHtml() &
            JsRaw("alert('Successfully logged in ')").cmd &
            JsRaw("$('#auth').modal('hide');").cmd &
            MainPage.removeWarn()
        } else JsRaw(s"$$('.password-error').text('Incorrect password!')").cmd
      case Empty =>
        JsRaw(s"$$('.email-error').text('Incorrect email!')").cmd
    }
  }

  def loginForm(ns: NodeSeq) = {
    var mail = ""
    var pass = ""
    val css = "#email-login" #> SHtml.ajaxText("", s => {
      mail = s
      checkMail(s , "mod-log-email")
    }) &
      "#password-login" #> SHtml.ajaxText("", pass = _, "type" -> "password") &
      ".login-btn" #> SHtml.ajaxButton("Войти", () => login(mail, pass))
    css(ns)
  }
}
