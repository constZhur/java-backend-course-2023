package edu.java.bot.kafka;

public record KafkaProducer(
    String keySerializer,
    String valueSerializer
) { }
