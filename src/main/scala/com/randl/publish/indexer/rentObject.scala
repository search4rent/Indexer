package com.randl.publish.indexer

import java.util.UUID
import org.elasticsearch.common.geo.GeoPoint

/**
 * Created with IntelliJ IDEA.
 * User: cgonzalez
 * Date: 7/22/13
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */
case class RentItem(
                     id: UUID,
                     description: String,
                     location: GeoPoint,
                     name: String,
                     picture: List[String],
                     price: Double,
                     category: List[String],
                     user: UUID
                     )