package com.fypbackend.spring_boot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppStartupLogger implements ApplicationRunner {

    @Value("${server.port:8081}")
    private int serverPort;

    @Value("${spring.data.rest.base-path:/api}")
    private String apiBasePath;

    @Value("${app.cors.allowed-origins:http://localhost:4250}")
    private String allowedOrigins;

    @Override
    public void run(ApplicationArguments args) {
        log.info("API base URL available at http://localhost:{}{}", serverPort, apiBasePath);
        log.info("CORS allowed origins -> {}", allowedOrigins);
    }
}

