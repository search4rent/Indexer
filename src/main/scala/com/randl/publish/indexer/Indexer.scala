package com.randl.publish.indexer

import com.randl.core.servicelib.elasticsearch.ESClient
import com.codahale.jerkson.Json
import java.util.UUID
import scala.collection.JavaConversions._
import java.lang.{Double => jDouble}

/**
 * Created with IntelliJ IDEA.
 * User: cgonzalez
 * Date: 10/7/13
 * Time: 9:01 AM
 * To change this template use File | Settings | File Templates.
 */
trait Indexer extends ESClient {


  def toRentItem(map: Map[String, Any]): Option[RentItem] =
    Option(RentItem(
      id = UUID.fromString(map.getOrElse("id", "-").asInstanceOf[String]),
      description = map.getOrElse("description", "-").asInstanceOf[String],
      location = null,
      name = map.getOrElse("name", "-").asInstanceOf[String],
      picture = map.getOrElse("picture", new java.util.ArrayList).asInstanceOf[java.util.ArrayList[String]].toList,
      price = map.getOrElse("price", "0") match {
        case y: java.lang.Long => new jDouble(y * 1D)
        case y: jDouble => y
        case _ => 0
      },
      category = map.getOrElse("category", new java.util.ArrayList).asInstanceOf[java.util.ArrayList[String]].toList,
      user = UUID.fromString(map.getOrElse("user", "-").asInstanceOf[String])
    ))

  def itemMapper(entry: Any): Option[RentItem] = {
    val map = entry.asInstanceOf[java.util.HashMap[String, Any]].toMap
    map.get("index") match {
      case Some(x: Boolean) =>
        if (!x) toRentItem(map) else None
      case None => toRentItem(map)
    }

  }


  def indexer(entry: Any): Boolean = {
    itemMapper(entry) match {
      case Some(x) =>
        val writeRequest = client.prepareIndex()
          .setIndex(indexES)
          .setType(typeES)
          .setId(x.id.toString)
          .setSource(Json.generate(x))
        val bulk = client.prepareBulk()
        bulk.add(writeRequest).execute().actionGet()
        true
      case None => false
    }

  }

  def delete(entry: Any) = {
    val map = entry.asInstanceOf[java.util.HashMap[String, Any]].toMap
    map.get("id") match {
      case Some(id: String) =>
        val deleteRequest = client.prepareDelete().setId(id)
        val bulk = client.prepareBulk()
        bulk.add(deleteRequest).execute().actionGet()
      case None =>
    }
  }


}
