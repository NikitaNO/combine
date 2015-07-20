package code

import code.model.{Rate, CurrencyType}
import net.liftweb.util.Helpers._
import org.apache.commons.io.IOUtils

object CurrencyHelpers {
  val yahooCurRequest = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22USDRUB%22,%22RUBUSD%22)&env=store://datatables.org/alltableswithkeys"

  def convert(from: CurrencyType.Value, to: CurrencyType.Value, amount: Double) = {
    val rates = getRate()
    val usdrub = rates(0)
    val rubusd = rates(1)

    from match {
      case CurrencyType.rub =>
        rubusd.bid * amount
      case CurrencyType.usd =>
        usdrub.bid * amount
    }
  }

  def getRate(): Seq[Rate] = {
    tryo {
      val source = scala.io.Source.fromURL(yahooCurRequest)
      val ns = xml.XML.loadString(source.mkString("").split("yahoo:lang=\"ru\">").last)//prologue end
      val rates = ns \\ "rate"
      for(rate <- rates) yield {
        val askRate = (rate \ "Ask").text.toDouble
        (rate \ "@id").text match {
          case "USDRUB" =>
            Rate(CurrencyType.usd, CurrencyType.rub, askRate)
          case "RUBUSD" =>
            Rate(CurrencyType.rub, CurrencyType.usd, askRate)
        }
      }
    } openOrThrowException "Something wrong with yahoo's currency REST"
  }
}