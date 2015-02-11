package com.pivotallabs.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("default")
public class OfflineConfig {

    public static final String DEFAULT_RABBIT_HOST = "localhost";

    @Bean
    public ConnectionFactory rabbitConnectionFactory() {
        String rabbitHost = System.getProperty("rabbitHost") != null ? System.getProperty("rabbitHost") : DEFAULT_RABBIT_HOST;
        return new CachingConnectionFactory(rabbitHost);
    }


}
