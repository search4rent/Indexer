package com.randl.publish

import com.randl.core.servicelib.configuration.Configuration

/**
 * Created with IntelliJ IDEA.
 * User: cesar
 * Date: 10/9/13
 * Time: 9:47 PM
 * To change this template use File | Settings | File Templates.
 */
package object indexer extends Configuration {
  val database: String = system.getString("itemsDataBase")
}
