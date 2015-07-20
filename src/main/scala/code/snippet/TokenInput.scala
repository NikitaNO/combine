//package code.snippet
//
///**
// * Created by Ni on 08.11.2014.
// */
//import net.liftweb.common.{Empty, Full}
//import net.liftweb.http.{SHtml, S}
//import net.liftweb.util.{Helpers, PassThru}
//import odesk.oDesk.ODeskParser
//import scala.xml.NodeSeq
//import java.util.Scanner
//import Helpers._
//import net.liftweb.http.js.JsCmds
//import bootstrap.liftweb.{ParseSession, PostingBotActor}
//
//
//class TokenInput {
//  var input = ""
//  val url = ParseSession.odeskParser.client.getAuthorizationUrl("oob")
//
//  def render(ns: NodeSeq) = {
//    val cssRule = "#authURL" #> {
//      SHtml.text(url,(String) => ())
//     } &
//    "#token" #> SHtml.ajaxText("",onTokenInput) &
//    "#submit" #> SHtml.submitButton(submit)
//    cssRule.apply(ns)
//  }
//
//  def onTokenInput(s: String) = {
//      input = s
//      JsCmds.Noop
//  }
//
//  def submit() = {
//    val x = S.param("token")
//    x match{
//      case Full(token) =>
//        ParseSession.odeskParser.token = token
//        PostingBotActor.initNextGetJobs()
//      case Empty =>
//    }
//    }
//}
