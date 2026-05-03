package com.tech_sys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
public class TechSysApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechSysApplication.class, args);
        System.out.println("txt");
    }


}
