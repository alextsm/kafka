- download kafka at  https://www.apache.org/dyn/closer.cgi?path=/kafka/0.11.0.1/kafka_2.11-0.11.0.1.tgz
- extract kafka in a folder
- open a shell - zookeeper is at localhost:2181
- \# bin/zookeeper-server-start.sh config/zookeeper.properties

- open another shell - kafka is at localhost:9092

- \# bin/kafka-server-start.sh config/server.properties

- create input topic

- \# bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic input-topic
- create other topics

- \# bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic currentdata

- \# bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic summing

- \# bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic counting

- \# bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic average

- \# bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic more25

- \# bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic less25

- start the producer to produce some data
- start the StreamsStarterApp to playing with the data 
- using the kafka-console-consumer to watch the results, for example 

- \# bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic input-topic \
    --from-beginning \
    --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.DoubleDeserializer

