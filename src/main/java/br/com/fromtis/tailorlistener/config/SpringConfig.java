package br.com.fromtis.tailorlistener.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAutoConfiguration
@ComponentScan("br.com.fromtis.tailorlistener.config")
public class SpringConfig {


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
