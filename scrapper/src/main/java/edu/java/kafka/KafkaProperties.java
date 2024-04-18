package edu.java.kafka;

public record KafkaProperties(
    String bootstrapServers,
    KafkaProducer kafkaProducer,
    String topicName
) { }
