package org.test.TestRedirect

import collection.mutable
import java.util.concurrent.ConcurrentHashMap
import collection.JavaConverters._


object UserStore {
  val retUsers: mutable.Map[String, String] = new ConcurrentHashMap[String, String].asScala
}
