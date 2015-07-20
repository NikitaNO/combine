package code
package model

import net.liftweb.common._
import net.liftweb.mapper._

/**
 * The singleton that has methods for accessing the database
 */
object User extends User with MetaMegaProtoUser[User]  {

  override def signupFields = List(email,password)

}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class User extends MegaProtoUser[User]  {

  override def primaryKeyField = id

  override lazy val email = new MyEmail(this, 50)

  override lazy val password = new MappedPassword(this){
    override def get = i_was_!
  }

  override def getSingleton = User
}

case class Credentials(email: String, password: String, passConf : Box[String])