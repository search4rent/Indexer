package com.randl.publish.indexer

import io.searchbox.client.config.ClientConfig
import io.searchbox.client.{JestClient, JestClientFactory}
import io.searchbox.indices.CreateIndex
import org.elasticsearch.common.settings.ImmutableSettings

/**
 * Created with IntelliJ IDEA.
 * User: cesar
 * Date: 10/27/13
 * Time: 12:12 PM
 * To change this template use File | Settings | File Templates.
 */
trait ElasticSearchRestIndexer {
  val ES_SERVER = "http://24ehmqpf:aiegxx8odfk5lvps@oak-165896.eu-west-1.bonsai.io"
  val clientConfig: ClientConfig =
    new ClientConfig
    .Builder("http://localhost:9200")
      .multiThreaded(true)
      .build()
  val factoryES: JestClientFactory = new JestClientFactory();
  factoryES.setClientConfig(clientConfig);
  val client: JestClient = factoryES.getObject();
  val settingsBuilder = ImmutableSettings.settingsBuilder();
  settingsBuilder.put("number_of_shards", 5);
  settingsBuilder.put("number_of_replicas", 1);
}
