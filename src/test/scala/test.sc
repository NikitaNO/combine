//import scalaz.Monad
//
//type Birds = Int
//
//case class Pole(left: Birds, right: Birds){
//  def landLeft(i: Birds): Option[Pole] = {
//    if(left + i - right > 3) None
//    else
//    Some(copy(left = left + i))
//  }
//
//  def landRight(i: Birds): Option[Pole] = {
//    if(right + i - left > 3) None
//    else
//    Some(copy(right = right + i))
//  }
//}
//
//Monad[Option].point(Pole(0,0)).flatMap(_.landLeft(2)).flatMap(_.landRight(10))
//Monad[Option].point(Pole(0,0)) >>= {_.landLeft(3)}


///гетерогенный список
sealed trait HList

case class HCons[H, T <: HList](head: H, tail: T) extends HList{
  def ::[T](v : T) = HCons(v, this)
}

object HNil extends HList{
  def ::[T](v: T) = HCons(v, HNil)
}

val a = "s" :: true :: HNil

a.tail.head