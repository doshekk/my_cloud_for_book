package org.example.filecloud.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {


    @Bean
    public RestClient BooksRestClient() {
        return RestClient.builder()
                .baseUrl("https://flibusta.site")
                .requestInterceptor(((request, body, execution) -> {
                    System.out.println("metod:" + request.getMethod());
                    System.out.println("URL:" + request.getURI());
                    return execution.execute(request,body);
                }))
                .build();
    }
}