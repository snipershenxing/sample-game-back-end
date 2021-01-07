package com.ethanliang.dropToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DropTokenApplication {
	private static final Logger log = LoggerFactory.getLogger(DropTokenApplication.class);
    
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
    	RestTemplate restTemplate = builder.build();
    	return restTemplate;
    }
    
	public static void main(String[] args) {
    	SpringApplication.run(DropTokenApplication.class);
    }    
}
