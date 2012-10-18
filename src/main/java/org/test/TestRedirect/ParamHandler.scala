package org.test.TestRedirect

import collection.mutable

object ParamHandler {
  def extractMoreKVs(lst: List[String]): Map[String, String] = lst match {
    case Nil => Map.empty[String, String]
    case s :: rest => {
      s.split('=').toList match {
        case k :: v :: Nil => Map(k -> v) ++ extractMoreKVs(rest)
        case _ => extractMoreKVs(rest)  // skip it, it wasn't formatted as key=value pair
      }
    }
  }
}
