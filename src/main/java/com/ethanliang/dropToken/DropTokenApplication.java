package com.ethanliang.dropToken;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@EnableAsync
@SpringBootApplication
public class DropTokenApplication {

	@Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
    }
    
	public static void main(String[] args) {
    	SpringApplication.run(DropTokenApplication.class);
    }    
}
