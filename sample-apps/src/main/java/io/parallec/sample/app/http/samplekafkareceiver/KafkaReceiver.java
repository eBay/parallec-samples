package io.parallec.sample.app.http.samplekafkareceiver;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

/**
 * 
 * Simple Kafka receiver to validate Parallec kafka producer logic.
 * tested. replace HOSTNAME before use.  
 * 
 * Check pom for the kafka version.
 * 
 * http://kafka.apache.org/documentation.html
 * @author Yuanteng (Jeff) Pei
 *
 */
public class KafkaReceiver {
	
    public static void main(String args[]) {
        
        Properties props = new Properties();
        props.put("zookeeper.connect", "HOSTNAME:2181");
        props.put("bootstrap.servers", "HOSTNAME:9092");
        props.put("group.id", "test");
        props.put("partition.assignment.strategy", "roundrobin");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        
        String topicId="parallec-topic";
        
        kafka.consumer.ConsumerConfig consumerConfig = new kafka.consumer.ConsumerConfig(props);
        
        ConsumerConnector consumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topicId, 1);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector.createMessageStreams(topicCountMap);
     
        List<KafkaStream<byte[], byte[]>> streamList = consumerMap.get(topicId);
     
        KafkaStream<byte[], byte[]> stream = streamList.get(0);
     
        ConsumerIterator<byte[], byte[]> iterator = stream.iterator();
        while(iterator.hasNext()) {
          System.err.println("\nKafka Subscriber received event: ");
          System.out.println(new String(iterator.next().message()));
        }
     
      }
     
    
    
}
