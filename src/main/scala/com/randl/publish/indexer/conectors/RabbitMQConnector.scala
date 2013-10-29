package com.randl.publish.indexer.conectors

import com.rabbitmq.client.{QueueingConsumer, ConnectionFactory}
import com.codahale.jerkson.Json
import com.randl.publish.indexer.ElasticSearchRestIndexer
import io.searchbox.core.{Delete, Get, Index}


case class Item(description: String, checkedOut: Boolean)

case class Message(id: String, event: String, body: Item)

object RabbitMQConnector extends App with ElasticSearchRestIndexer {
  //  def connect = {
  println("Start")
  val user = "kxxpfkfn"
  val password = "KtYqkmYZT6Y9Fl2GYl7qBUMo4y5z-ZbT"
  //var uri = "amqp://" + user + ":" + password + "@bunny.cloudamqp.com/" + user
  val uri = "amqp://guest:guest@localhost";

  val factory = new ConnectionFactory();
  factory.setUri(uri)
  val connection = factory.newConnection();
  val channel = connection.createChannel();

  val consumer = new QueueingConsumer(channel);

  channel.basicConsume("randl", true, consumer);



  while (true) {
    val delivery: QueueingConsumer.Delivery = consumer.nextDelivery();
    println(" [x] Message '" + new String(delivery.getBody()) + "'");

    val message = Json.parse[Message](new String(delivery.getBody()))
    val body = message.event match {
      case "update" => message.body
      case "checkout" => {
        val search = new Get.Builder("randl", message.id).`type`("item").build();
        val result = client.execute(search)
        val item: Item = result.getSourceAsObject(classOf[Item])

        item.copy(checkedOut = true)
      }
      case "checkin" => {
        val search = new Get.Builder("randl", message.id).`type`("item").build();
        val result = client.execute(search)
        val item: Item = result.getSourceAsObject(classOf[Item])

        item.copy(checkedOut = false)
      }
      case "remove" => {
        val search = new Delete.Builder("randl", message.id, "item").build();
        val result = client.execute(search)
        val item: Item = result.getSourceAsObject(classOf[Item])

        item.copy(checkedOut = true)
      }
      case _ => null
    }
    println(" [x] Message '" + Json.generate(message) + "'");
    println(" [x] Json '" + Json.generate(body) + "'");
    val index = new Index.Builder(body).id(message.id).index("randl").`type`("item").build()
    val writeRequest = client.execute(index)
    println("request -> ", writeRequest)
  }
}
