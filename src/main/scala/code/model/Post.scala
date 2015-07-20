package code
package model

import java.util.Date

import _root_.google.GoogleLinkShorter
import code.Settings.Feed
import net.liftweb.common.{Loggable, Box}
import net.liftweb.mapper._
import net.liftweb.util.Helpers._
import MyHelpers._

import scala.Double


case class Rate(cur: CurrencyType.Value, cur2: CurrencyType.Value, bid: Double)

object CurrencyType extends Enumeration {
  val rub = Value(1, "Ruble")
  val usd = Value(2, "Dollar")
}

class PostModel extends LongKeyedMapper[PostModel] {
  implicit def field2FieldType(mf: MappedField[_,_]) = mf.get

  def getSingleton = PostModel

  def primaryKeyField = id

  object id extends MappedLongIndex(this)

  object title extends MappedString(this, 200)

  object description extends MappedString(this, 2000)

  object link extends MappedString(this, 120)

  object date extends MappedDate(this)

  object budget extends MappedString(this,30) with LifecycleCallbacks{
    override def beforeSave: Unit ={
      val (rAmount,uAmount): (Double,Double) =
        tryo/*(logException _)*/{
          val num = budget.get
            .replaceAll("\\D","").toDouble
          originalCurrency.get match {
            case CurrencyType.rub =>
              val converted = CurrencyHelpers.convert(originalCurrency, CurrencyType.usd, num)
              (num, converted)
            case CurrencyType.usd =>
              val converted = CurrencyHelpers.convert(originalCurrency, CurrencyType.rub, num)
              (converted, num)
          }
        } openOr (0d,0d)
      rubAmount(rAmount).usdAmount(uAmount)
    }
  }

  object published extends MappedBoolean(this)

  object feed extends MappedEnum(this, Feed)

  object descriptionNodeSeq extends MappedString(this,3000) // todo remake , it's just workaround

  object rubAmount extends MappedDouble(this)

  object usdAmount extends MappedDouble(this)

  object originalCurrency extends MappedEnum(this, CurrencyType)

  object responses extends MappedInt(this)

  def budgetLabel(c: CurrencyType.Value) = c match {
    case CurrencyType.rub => "" + decimalFormat.format(rubAmount.get) + "Р."
    case CurrencyType.usd => "" + decimalFormat.format(usdAmount.get) + "$"
  }

  override def toString = {
    val l = if(link == null) "" else GoogleLinkShorter.shorten(link.get).trim
    val t = if(title == null) "" else title.get.trim
    val d = if(description == null) "" else description.get.trim
    val s = f"$l\n${t.replace("  "," ").replace("  ","")}\n$d"
    if (s.length >= 130) s.substring(0,130) + "..."
    else s
  }

  def toTweet: String = {
    val l = if(link == null) "" else GoogleLinkShorter.shorten(link.get).trim
    val t = if(title == null) "" else title.get.trim
    val d = if(description == null) "" else description.get.trim
    feed.get match{
      case Feed.weblancer =>
        val emptyBudgPat = "Бюджет : ?"
        val descr = if(d.contains(emptyBudgPat)) {
          val start = description.indexOf(emptyBudgPat)
          val end = start + emptyBudgPat.length
          description.get.split(emptyBudgPat).toList.foldLeft("")(_ + _)
        } else description.get
        f"$l\n${t.replace("  "," ").replace("  ","")}\n$descr"
      case _ =>
        f"$l\n${t.replace("  "," ").replace("  ","")}\n$d"
    }
  }
}

object PostModel extends PostModel with LongKeyedMetaMapper[PostModel] with Loggable {
  def apply(title: String,description: String,link: String,date : Box[Date],
            budget: String, feedType: Feed.Value, currency : CurrencyType.Value,
            responses : Int = 0 , save: Boolean= true) = {
      PostModel.find(By(PostModel.title, title)).openOr {
        val p = PostModel.create
          .title(title)
          .description(description)
          .link(link)
          .date(date.openOr(null))
          .budget(budget)
          .feed(feedType)
          .originalCurrency(currency)
          .responses(responses)
        tryo(logException(_)){
          if(save) p.save()
        }
        p
      }
  }
}


//object WebLancerPost extends PostModel {
//  def apply(title_ : String, description_ : String, link_ : String, date_ : Box[Date], budget_ : String) = {
//    PostModel(title_, newDescription, link_, date_, budget_, FeedType.weblancer)
//  }
//}