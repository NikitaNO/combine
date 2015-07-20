package bootstrap.liftweb

import java.io.File
import java.net.URL
import java.nio.file.{Paths, Path, Files}
import java.util.Date
import javax.mail.{PasswordAuthentication, Authenticator}

import code.rest.JobsRest
import net.liftweb._
import net.liftweb.http.provider.HTTPParam
import util._
import Helpers._

import common._
import http._
import js.jquery.JQueryArtifacts
import sitemap._
import Loc._
import mapper._

import code.model._
import net.liftmodules.JQueryModule
import code.{MyHelpers, TestInitParams, InitParams}


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */

object ParseSession {
  val postsBot = new MainActor(TestInitParams)
}

class Boot {
  def boot {
    java.security.Security.removeProvider("OracleUcrypto")
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor =
        new StandardDBVendor(/*Props.get("db.driver") openOr */"org.h2.Driver", // todo replace to props
          //			     Props.get("db.url") openOr
          "jdbc:h2:db/lift_proto.db;MVCC=TRUE;AUTO_SERVER=TRUE;LOCK_TIMEOUT=10000",
          Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)
      DB.defineConnectionManager(util.DefaultConnectionIdentifier, vendor)
    }

    Schemifier.schemify(true, Schemifier.infoF _, User, PostModel, EmailSubscriber)

    LiftRules.addToPackages("code")

    LiftRules.dispatch.append(JobsRest)
    def sitemap = SiteMap(
      Menu.i("Home") / "index",
      Menu.i("Admin") / "admin",
      Menu.i("Main") / "main" >> User.AddUserMenusAfter, // the simple way to declare a menu

      Menu(Loc("Static", Link(List("static"), true, "/static/index"),
        "Static Content")))

    def sitemapMutators = User.sitemapMutator

    LiftRules.setSiteMapFunc(() => sitemapMutators(sitemap))

    LiftRules.jsArtifacts = JQueryArtifacts
    JQueryModule.InitParam.JQuery = JQueryModule.JQuery191
    JQueryModule.init()

    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.ajaxPostTimeout = 60000

    LiftRules.supplementalHeaders.default.set(
        List(
          "Access-Control-Allow-Origin" -> "*",
          "Access-Control-Allow-Credentials" -> "true",
          "Access-Control-Allow-Methods" -> "GET, POST, PUT, OPTIONS",
          "Access-Control-Allow-Headers" -> "WWW-Authenticate,Keep-Alive,User-Agent,X-Requested-With,Cache-Control,Content-Type"
        )
    )
        LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

        LiftRules.htmlProperties.default.set((r: Req) =>
          new Html5Properties(r.userAgent))

        S.addAround(DB.buildLoanWrapper)

    val pathToLog4jxml = "logs/log-" + MyHelpers.dateFormat.format(new Date) + ".txt"
    val logFile = new File(pathToLog4jxml)
    if(!logFile.exists()) {
      Files.createFile(Paths.get(pathToLog4jxml))
    }
    Logger.setup = Full(Log4j.withFile(logFile.toURL))
    Mailer.authenticator = Full(new Authenticator {
      override def getPasswordAuthentication = new PasswordAuthentication("combine.freelance@gmail.com", "combine12")
    })

        ParseSession.postsBot.initScripts()
    }

}
