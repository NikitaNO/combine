package code.model

import net.liftweb.mapper.{MappedString, LongKeyedMapper, LongKeyedMetaMapper, MappedLongIndex}

class EmailSubscriber extends LongKeyedMapper[EmailSubscriber]{
  def getSingleton = EmailSubscriber

  def primaryKeyField = id

  object id extends MappedLongIndex(this)

  object email extends  MappedString(this,50)
}

object EmailSubscriber extends EmailSubscriber with LongKeyedMetaMapper[EmailSubscriber]