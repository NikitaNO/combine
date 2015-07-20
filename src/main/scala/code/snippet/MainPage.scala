package code.snippet

import java.util.Date

import bootstrap.liftweb.ParseSession
import code.Filters._
import code.MyHelpers._
import code.Settings.Feed
import code.model.{CurrencyType, PostModel, User}
import code.{MyHelpers, TestInitParams}
import net.liftweb.common.{Box, Empty, Full}
import net.liftweb.http._
import net.liftweb.http.js.JE.{JsFunc, JsRaw}
import net.liftweb.http.js.{JsExp, JsCmds}
import net.liftweb.util.Helpers._
import org.joda.time.DateTime

import scala.xml.{NodeSeq, Text, XML}

object MainPage {

  val url ="/main"
  object RedrawPosts extends SessionVar[Box[IdMemoizeTransform]](Empty)
  object RedrawPagination extends SessionVar[Box[IdMemoizeTransform]](Empty)
  object RedrawBotPagination extends SessionVar[Box[IdMemoizeTransform]](Empty)
  object RedrawHeader extends SessionVar[Box[IdMemoizeTransform]](Empty)
  object Feeds extends SessionVar[Map[Feed.Value, Boolean]](TestInitParams.feeds.map(_.feed -> true).toMap)

  def removeWarn() = JsFunc("removeWarn()").cmd
}

class MainPage extends PaginatorSnippet[PostModel]  {
  import code.snippet.MainPage._

  private var postsList: List[PostModel] = Nil   //change only by setPostsList
  private var curPageVar = 0
  private var sortByVar = SortBy.desc
  private val postNumbers = Seq(25, 50, 75, 100)
  private val postNumbersMap = (postNumbers.map(pn => "" + pn + " шт.") zip postNumbers).toMap
  private var postsPerPage = postNumbers(0)
  private var currency = CurrencyType.usd

  setPostsList(MyHelpers.allPosts)

  def setPostsList(posts: List[PostModel]){
    val withDateAndBudg = {
      val l = posts.filterNot(_.date.get == null).filterNot(_.rubAmount.get == 0)
      sortByVar match {
        case SortBy.asc =>
          l.sortBy(_.date.get)
        case SortBy.desc =>
          l.sortBy(_.date.get).reverse
        case SortBy.ascBudg =>
          l.sortBy(_.rubAmount.get)
        case SortBy.descBudg =>
          l.sortBy(_.rubAmount.get).reverse
      }
    }
    val withoutDate = posts.filter(_.date.get == null)
    val withoutBudg = posts.filter(_.rubAmount.get == 0)
    postsList = withDateAndBudg ::: withoutDate ::: withoutBudg
  }

  def renderPosts = SHtml.idMemoize(outer => {
    RedrawPosts set Full(outer); (ns: NodeSeq) => posts
  })

  def renderHeader = SHtml.idMemoize(outer => {
    RedrawHeader set Full(outer); (ns: NodeSeq) => navbar(ns)
  })

  def renderPagination = SHtml.idMemoize(outer => {
    RedrawPagination set Full(outer); (ns: NodeSeq) => pagination
  })

  def renderBottomPagination = SHtml.idMemoize(outer => {
    RedrawBotPagination set Full(outer); (ns: NodeSeq) => pagination
  })

  def currencyRub = SHtml.ajaxButton("РУБ", () => {
    currency = CurrencyType.rub
    redrawPage() &
    JsRaw("$('#currency-rub').removeClass('active-currency');$('#currency-usd').addClass('active-currency');").cmd
  },"class" -> "link-btn active-currency", "id" -> "currency-rub")

  def currencyUsd = SHtml.ajaxButton("USD", () => {
    currency = CurrencyType.usd
    redrawPage() &
    JsRaw("$('#currency-usd').removeClass('active-currency');$('#currency-rub').addClass('active-currency');").cmd
  },"class" -> "link-btn", "id" -> "currency-usd")

  def navbar(ns: NodeSeq) = {
    val css = if (User.currentUser.isDefined) {
      "#login" #> NodeSeq.Empty &
      "#homeBtn [href]" #> NodeSeq.Empty &
        "#signup" #> NodeSeq.Empty &
        "#welcome-label *" #> <a>
          {s"Welcome, ${User.currentUser.get.email}"}
        </a> &
        "#logout" #> <li>{ SHtml.link("", () => {
          User.logoutCurrentUser
          S.redirectTo("/") // todo remake to index
        }, Text("Logout")) }</li>
    }
    else
      "#welcome-label" #> NodeSeq.Empty &
        "#logout" #> NodeSeq.Empty
    css(ns)
  }

  def redrawAuthorized() = RedrawPosts.is.get.setHtml() &
    RedrawHeader.is.get.setHtml() & JsRaw("getAllElements()").cmd

  def redrawPage() = RedrawPosts.is.get.setHtml() &
    RedrawPagination.is.get.setHtml() & JsRaw("getAllElements()").cmd

  def filters(ns: NodeSeq): NodeSeq = {
    var text: Box[String] = Empty
    var fromDate: Box[Date] = Empty
    var untilDate: Box[Date] = Empty
    var fromBudg: Box[Float] = Empty
    var untilBudg: Box[Float] = Empty


    def refresh = {
      val filtered = MyHelpers.allPosts
        .filterPosts(fromDate, untilDate, text, fromBudg, untilBudg,currency, Feeds.is.filter(_._2).keys.toSet)
      setPostsList(filtered)
      curPageVar = 0
      redrawPage()
    }

    val css =
      "#feeds" #> TestInitParams.feeds
        .map {
        feed =>
          val checkbox = SHtml.ajaxCheckbox(Feeds.is(feed.feed), b => {
            Feeds.set(Feeds.is ++ Map(feed.feed -> b))
            refresh
          }, "class" -> "check")
          <label>
            {checkbox}{feed.feed.toString}
          </label>
      }.foldLeft(NodeSeq.Empty)(_ ++ _) &
        "#text" #> SHtml.ajaxText("", s => {
          text = Full(s)
          refresh
        }) &
        "#fromDate" #> SHtml.ajaxText("", s => {
          tryo {
            fromDate = if(s.isEmpty) Full(DateTime.now.withYearOfEra(0).toDate)
            else Full(s.toFormatDate)
            refresh
          } openOr JsCmds.Noop
        }) &
        "#untilDate" #> SHtml.ajaxText("", s => {
          tryo {
            untilDate = if(s.isEmpty) Full(DateTime.now.withYearOfEra(10000).toDate)
            else Full(s.toFormatDate)
            refresh
          } openOr JsCmds.Noop
        }) &
        "#min-amount" #> SHtml.ajaxText("", s => {
          tryo {
            fromBudg = Full(s.toBudget)
            refresh
          } openOr JsCmds.Noop
        }) &
        "#max-amount" #> SHtml.ajaxText("", s => {
          tryo {
            untilBudg = Full(s.toBudget)
            refresh
        } openOr JsCmds.Noop
        })
    css(ns)
  }

  def buttons(ns: NodeSeq): NodeSeq = {
    val css =
      "#btn-register" #> SHtml.ajaxButton(Text("Register"), () => JsRaw(s"$$('#registr').modal('show')").cmd) &
        "#btn-start" #> SHtml.ajaxButton(Text("Start"), () => {
          ParseSession.postsBot.initScripts()
          JsRaw("$('#btn-start').addClass('disabled')").cmd
        })
    css.apply(ns)
  }

  def pagination: NodeSeq = {
    def setCurPage(i: Int): Unit = {
      if (i >= 0 && i < numPages)
        curPageVar = i
    }

    def colorSelected(pos: Int) = {
      JsRaw(s"$$('.pagination').removeClass('selected')").cmd &
        JsRaw(s"$$('.${(pos + 1).toString}-pagin').addClass('selected');").cmd
    }

    def btn(label: String, onClick: () => Unit, attrs: Seq[SHtml.ElemAttr] = Nil) = {
      SHtml.ajaxButton(Text(label), () => {
        onClick.apply()
        redrawPage() &
          colorSelected(curPageVar)
      }, (Seq(SHtml.ElemAttr.pairToBasic("class" -> s"list-item $label-pagin")) ++ attrs): _*)
    }

    val first = btn("<< <<", () => setCurPage(0))
    val prev = btn("<<", () => setCurPage(curPageVar - 1))
    val next = btn(">>", () => setCurPage(curPageVar + 1))
    val last = btn(">> >>", () => setCurPage(numPages - 1))
    val numsOnPage = 10
    val totalNum = (count / postsPerPage).toInt
    val firstNum = if (totalNum < numsOnPage) 0 else (curPage / numsOnPage) * 10
    val lastNum = if (totalNum >= (firstNum + (numsOnPage - 1))) firstNum + (numsOnPage - 1) else totalNum
    val nums = for (i <- firstNum to lastNum) yield {
      val attr = if (i == curPage) Seq(SHtml.ElemAttr.pairToBasic("class" -> s"list-item ${i + 1}-pagin active")) else Nil
      btn((i + 1).toString, () => curPageVar = i, attr)
    }
    first ++ prev ++ nums ++ next ++ last
  }

  def posts: NodeSeq = {
    val template = "post-template" :: Nil
    val posts = page
    def css(p: PostModel) = {
      val date = Some(p.date)
      val link = if (User.currentUser.isDefined)
        SHtml.link(p.link.get, () => (), Text("Full >>"), "href" -> p.link.get, "target" -> "_blank")
      else NodeSeq.Empty
      val description = {
        val descrNodeSeq = p.descriptionNodeSeq.get
        if (descrNodeSeq != null && descrNodeSeq.nonEmpty) XML.loadString(descrNodeSeq)
        else Text(p.description.get)
      }

        "data-bind=info-block [class]" #> ("col-md-3 " + posts.indexOf(p).toString) &
        "data-bind=post-container [data-id]" #> posts.indexOf(p) &
        "data-bind=context [id]" #> posts.indexOf(p) &
        "data-bind=title" #> <h3>{p.title.get}</h3> &
        "data-bind=site *" #> Text(p.feed.toString()) &
        "data-bind=description *" #> (description ++ link) &
        "data-bind=budget *" #> Text(p.budgetLabel(currency)) &
        "data-bind=date *" #> Text(date.map(_.toString).getOrElse(""))
    }
    posts.map(css(_)(template)).foldLeft(NodeSeq.Empty)(_ ++ _)
  }

  def postsNumberSelector: NodeSeq = SHtml.ajaxSelectElem(postNumbersMap.keySet.toSeq, Full(postNumbersMap.head._1))(p => {
    postsPerPage = postNumbersMap(p)
    curPageVar = 0
    redrawPage()
  })

  def currencySelector : NodeSeq = SHtml.ajaxSelectElem(CurrencyType.values.toSeq, Full(CurrencyType.usd))(c => {
  currency = c
  redrawPage()
  })

  def warning(ns: NodeSeq) = if (User.loggedIn_?) NodeSeq.Empty else ns

  import code.snippet.SortBy._

  def sortBySelector: NodeSeq = SHtml.ajaxSelectElem(Seq(asc, desc, ascBudg, descBudg), Full(desc))(p => {
    sortByVar = p
    setPostsList(postsList)
    curPageVar = 0
    redrawPage()
  })

  override def count: Long = postsList.size

  override def page: Seq[PostModel] = postsList.slice(curPage * itemsPerPage, curPage * itemsPerPage + itemsPerPage)

  override def curPage = curPageVar

  override def itemsPerPage = postsPerPage
}

object SortBy extends Enumeration {
  type SortBy = Value
  val asc = Value("От старых к новым")
  val desc = Value("От новых к старым")
  val ascBudg = Value("От дешевых к дорогим")
  val descBudg = Value("От дорогих к дешевым")
}

