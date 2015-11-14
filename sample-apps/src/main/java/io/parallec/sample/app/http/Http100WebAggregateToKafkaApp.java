package io.parallec.sample.app.http;

import io.parallec.core.HostsSourceType;
import io.parallec.core.ParallecResponseHandler;
import io.parallec.core.ParallelClient;
import io.parallec.core.ResponseOnSingleTask;
import io.parallec.core.util.PcDateUtils;
import io.parallec.core.util.PcStringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 * check POM to see which kafka version we use.
 * 
 * Tested with the sample kafka receiver
 * 
 * replace HOSTNAME before usage
 * 
 * Hitting 100 common websites to get these status code and send to Kafka.
 * 
 * Start the io.parallec.sample.app.http.samplekafkareceiver: KafkaReceiver to validate if this works. 
 * 
 * if need to load target hosts from local .setTargetHostsFromLineByLineText(
 * "userdata/sample_target_hosts_top100_old.txt", HostsSourceType.LOCAL_FILE)
 * 
 * @author Yuanteng (Jeff) Pei
 */
public class Http100WebAggregateToKafkaApp {

    public static String serverUrl = "HOSTNAME:9092";
    
    public static KafkaProducer<String, String> generateProducer(String serverUrl) {
        KafkaProducer<String, String> producerKafka = null;
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverUrl);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        producerKafka = new KafkaProducer<String, String>(props);
        return producerKafka;
    }

    public static void main(String[] args) {
        ParallelClient pc = new ParallelClient();
        
        KafkaProducer<String, String> producerKafka = generateProducer(serverUrl);// kafka
                                                                 // client
                                                                 // initialize
        HashMap<String, Object> responseContext = new HashMap<String, Object>();
        responseContext.put("producerKafka", producerKafka);
        pc.prepareHttpGet("")
                .setConcurrency(1000)
                .setTargetHostsFromLineByLineText(
                        "http://www.parallec.io/userdata/sample_target_hosts_top100_old.txt",
                        HostsSourceType.URL)
                .setResponseContext(responseContext)
                .execute(new ParallecResponseHandler() {
                    public void onCompleted(ResponseOnSingleTask res,
                            Map<String, Object> responseContext) {
                        Map<String, Object> metricMap = new HashMap<String, Object>();
                        metricMap.put("StatusCode", res.getStatusCode()
                                .replaceAll(" ", "_"));
                        metricMap.put("LastUpdated",
                                PcDateUtils.getNowDateTimeStrStandard());
                        metricMap.put("HostName", res.getHost());
                        metricMap.put("NodeGroupType", "Web100");
                        String topic = "parallec-topic";
                        @SuppressWarnings("unchecked")
                        KafkaProducer<String, String> producerKafka = (KafkaProducer<String, String>) responseContext.get("producerKafka");
                        ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>(
                                topic, res.getHost(), PcStringUtils.renderJson(metricMap));
                        producerKafka.send(producerRecord);
                    }
                });
        producerKafka.close();
        pc.releaseExternalResources();
    }

}
