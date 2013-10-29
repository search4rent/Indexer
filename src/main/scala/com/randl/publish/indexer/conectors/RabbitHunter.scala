package com.randl.publish.indexer.conectors

import com.rabbitmq.client.{QueueingConsumer, ConnectionFactory}
import com.codahale.jerkson.Json
import com.randl.publish.indexer.RentItem
import com.randl.core.servicelib.elasticsearch.ESClient

/**
 * Created with IntelliJ IDEA.
 * User: cesar
 * Date: 10/27/13
 * Time: 2:18 PM
 * To change this template use File | Settings | File Templates.
 */
trait RabbitHunter extends ESClient {
  def reader[T](indexer: (T) => Unit)(implicit manifest: Manifest[T]) = {

    val uri = "amqp://guest:guest@localhost";

    val factory = new ConnectionFactory();

    factory.setUri(uri)
    val connection = factory.newConnection();
    val channel = connection.createChannel();
    val consumer = new QueueingConsumer(channel);
    channel.basicConsume("randl", true, consumer);

    while (true) {
      val delivery: QueueingConsumer.Delivery = consumer.nextDelivery();
      val message = Json.generate(delivery.getBody)

      println(" [x] Received '" + message + "'");
      val writeRequest = client.prepareIndex()
        .setIndex(indexES)
        .setType(typeES)
        .setSource(message)
      val bulk = client.prepareBulk()
      val jsonQuery = Json.generate(bulk.add(writeRequest))
      println(jsonQuery)
    }
  }
}
