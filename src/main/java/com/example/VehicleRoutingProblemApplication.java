package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.example.mapper")
@SpringBootApplication
public class VehicleRoutingProblemApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehicleRoutingProblemApplication.class, args);
    }

}
