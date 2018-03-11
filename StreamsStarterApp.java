package com.github.alextsm.kafka;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.*;
import java.util.Properties;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;


public class StreamsStarterApp {

    public static void main(String[] args) {

        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Double().getClass());

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Double> inputTopicData = builder.stream("input-topic");

        // Save current data from topic to KTable using intermediatetopic
        KStream<String, Double> intermediatetopic = inputTopicData.filter((key, value) -> value >= 0);
        intermediatetopic.to("currentdata", Produced.with(Serdes.String(), Serdes.Double()));
        KTable<String, Double> currentdata = builder.table("currentdata");

        // Just for output key:value to console to control the input data 
        inputTopicData.foreach((key, value) -> System.out.println(key + " =>---------------------- " + value));
        // Group by key
        KGroupedStream<String, Double> groupedStream = inputTopicData.groupByKey();


        // Calculating sum of values
       KTable<String, Double> summing = groupedStream

                 .reduce(((s1, s2) ->  s1 + s2 ), Materialized.as("SUM"));

        // Calculating counts of values
       KTable<String, Long> counting = inputTopicData

                .groupByKey()
                .count(Materialized.as("Count"));

        // Calculating average of values
        KTable<String, Double> average = summing.join(counting, (sum, count) -> sum.doubleValue()/count.doubleValue());

        // Route values to deference topics
        KStream<String, Double> more25 = inputTopicData.filter((key, value) -> value >= 2.5);
        KStream<String, Double> less25 = inputTopicData.filter((key, value) -> value < 2.5);

        // Save data to topics
        summing.toStream().to("summing", Produced.with(Serdes.String(), Serdes.Double()));
        counting.toStream().to("counting", Produced.with(Serdes.String(), Serdes.Long()));
        average.toStream().to("average", Produced.with(Serdes.String(), Serdes.Double()));
        currentdata.toStream().to("currentdata", Produced.with(Serdes.String(), Serdes.Double()));
        more25.to("more25", Produced.with(Serdes.String(), Serdes.Double()));
        less25.to("less25", Produced.with(Serdes.String(), Serdes.Double()));

        KafkaStreams streams = new KafkaStreams(builder.build(), config);
                 streams.start();

                // print the topology
                streams.localThreadsMetadata().forEach(data -> System.out.println(data));

                // shutdown hook to correctly close the streams application
                Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        // print the topology every 10 seconds for testing purposes
        while(true){
            streams.localThreadsMetadata().forEach(data -> System.out.println(data));
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                break;
            }
        }


    }
}
