package com.pivotallabs.web;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("default")
public class OfflineConfig {

    public static final String DEFAULT_RABBIT_HOST = "localhost";
    public static final String QUEUE_NAME = "myqueue";

    @Bean
    public ConnectionFactory connectionFactory() {
        String rabbitHost = System.getProperty("rabbitHost") != null ? System.getProperty("rabbitHost") : DEFAULT_RABBIT_HOST;
        return new CachingConnectionFactory(rabbitHost);
    }

    @Bean
    public RabbitAdmin amqpAdmin() {
        RabbitAdmin amqpAdmin = new RabbitAdmin(connectionFactory());
        amqpAdmin.declareQueue(new Queue(QUEUE_NAME));
        return amqpAdmin;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    @Autowired
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
