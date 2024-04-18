package edu.java.bot.kafka;

public record KafkaProperties(
    String bootstrapServers,
    KafkaProducer kafkaProducer,
    KafkaConsumer kafkaConsumer,
    String topicName,
    String dlqTopicName
) { }
