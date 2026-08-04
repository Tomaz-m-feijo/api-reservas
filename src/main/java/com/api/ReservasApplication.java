package com.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.api")
public class ReservasApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservasApplication.class, args);
    }
}