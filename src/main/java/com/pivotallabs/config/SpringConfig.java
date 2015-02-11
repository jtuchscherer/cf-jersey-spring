package com.pivotallabs.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.pivotallabs.orm", "com.pivotallabs.web", "com.pivotallabs.config"})
public class SpringConfig {

}
