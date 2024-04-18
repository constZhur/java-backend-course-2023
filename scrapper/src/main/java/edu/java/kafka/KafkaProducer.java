package edu.java.kafka;

public record KafkaProducer(
    String keySerializer,
    String valueSerializer
) { }
