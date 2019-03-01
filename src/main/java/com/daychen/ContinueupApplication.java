package com.daychen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class ContinueupApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContinueupApplication.class, args);
        System.out.println("======================================启动完成===================================");
    }

}
