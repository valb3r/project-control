package com.valb3r.projectcontrol.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.web.support.OpenSessionInViewInterceptor;
import org.springframework.web.servlet.handler.MappedInterceptor;

// Required due to: https://stackoverflow.com/questions/44907670/spring-data-rest-with-neo4j-how-to-remove-relationship
// Causes problems with clustered setup: https://info.michael-simons.eu/2020/02/03/spring-data-neo4j-neo4j-ogm-and-osiv/
// Still is required for proper transaction wrapping of the request
@Configuration
public class OpenInViewSpringDataInterceptor {

    @Bean
    public OpenSessionInViewInterceptor openSessionInViewInterceptor() {
        return new OpenSessionInViewInterceptor();
    }

    @Bean
    public MappedInterceptor myMappedInterceptor(OpenSessionInViewInterceptor interceptor) {
        return new MappedInterceptor(new String[] {"/**"}, interceptor);
    }
}
