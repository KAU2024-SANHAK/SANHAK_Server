package org.kau.kkoolbeeServer.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate=new RestTemplate();
        // JSON 메시지 컨버터 추가
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return new RestTemplate();
    }
}
