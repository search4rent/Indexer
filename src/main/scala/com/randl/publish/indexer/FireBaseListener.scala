package com.randl.publish.indexer

import com.firebase.client.{Firebase, ChildEventListener, DataSnapshot}
import com.randl.core.servicelib.elasticsearch.ElasticSearchFactory

/**
 * Created with IntelliJ IDEA.
 * User: cgonzalez
 * Date: 10/7/13
 * Time: 8:55 AM
 * To change this template use File | Settings | File Templates.
 */
object IndexerObject extends Indexer with App {

  val indexES = "rendl"
  val typeES = "item"

  ElasticSearchFactory.init()

  println("initialization fo the FireBase Listener")
  val usersRef = new Firebase(database);
  usersRef.addChildEventListener(new ChildEventListener() {
    @Override
    def onChildAdded(snapshot: DataSnapshot, previousChildName: String) {
      println("item added")
      indexer(snapshot.getValue)
    }

    @Override
    def onChildChanged(snapshot: DataSnapshot, previousChildName: String) {
      println("item update")
      indexer(snapshot.getValue)
    }

    @Override
    def onChildRemoved(snapshot: DataSnapshot) {
      println("item delete: " + snapshot.getName)
      delete(snapshot.getValue)
    }

    @Override
    def onChildMoved(snapshot: DataSnapshot, previousChildName: String) {

    }

    @Override
    def onCancelled() {

    }
  })

}
