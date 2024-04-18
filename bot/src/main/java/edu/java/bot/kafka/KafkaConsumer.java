package edu.java.bot.kafka;

public record KafkaConsumer(
    String groupId,
    String mappings
) { }
