package com.github.simplesteph.kafka.streams.course;
import java.util.Properties;
import java.util.Random;
import java.util.List;

//import simple producer packages
import org.apache.kafka.clients.producer.Producer;

//import KafkaProducer packages
import org.apache.kafka.clients.producer.KafkaProducer;

//import ProducerRecord packages
import org.apache.kafka.clients.producer.ProducerRecord;
import java.text.*;


//Create java class named “SimpleProducer”
public class producer {

    public static void main(String[] args) throws Exception{


        //Assign topicName to string variable
        String topicName = "input-topic";
        // create instance for properties to access producer configs   
        Properties props = new Properties();

        //Assign localhost id
        props.put("bootstrap.servers", "localhost:9092");

        //Set acknowledgements for producer requests.      
        props.put("acks", "all");

        //If the request fails, the producer can automatically retry,
        props.put("retries", 0);

        //Specify buffer size in config
        props.put("batch.size", 16384);

        //Reduce the no of requests less than 0   
        props.put("linger.ms", 1);

        //The buffer.memory controls the total amount of memory available to the producer for buffering.   
        props.put("buffer.memory", 33554432);

        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        props.put("value.serializer",
                "org.apache.kafka.common.serialization.DoubleSerializer");


        Producer<String, Double> producer = new KafkaProducer
                <String, Double>(props);

        double Low = 1.50;
        double High = 3.50;

        for(int i = 0; i < 1; i++) {

            double random = new Random().nextDouble();
            double result = Low + (random * (High - Low));

            // cut the double format
            DecimalFormat numberFormat = new DecimalFormat("#.00");

            String result_s = numberFormat.format(result);
            double result_d = Double.parseDouble(result_s);
            int r = (int) (Math.random() * 4);
            String name = new String[]{"APL", "GOOG", "AMZN", "ASX"}[r];
            producer.send(new ProducerRecord<String, Double>(topicName, name, result_d));
            System.out.println("Message sent successfully");

        }
        producer.close();

    }

}