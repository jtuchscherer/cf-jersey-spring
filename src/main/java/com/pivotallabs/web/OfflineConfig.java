package com.pivotallabs.web;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("default")
public class OfflineConfig {

	public static final String DEFAULT_RABBIT_HOST = "localhost";

	@Bean
	public ConnectionFactory connectionFactory() {
		String rabbitHost = System.getProperty("rabbitHost") != null ? System.getProperty("rabbitHost") : DEFAULT_RABBIT_HOST;
		return new CachingConnectionFactory(rabbitHost);
	}

	@Bean
	public AmqpAdmin amqpAdmin() {
		return new RabbitAdmin(connectionFactory());
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		return new RabbitTemplate(connectionFactory());
	}

	@Bean
	public Queue myQueue() {
		return new Queue("myqueue");
	}
}