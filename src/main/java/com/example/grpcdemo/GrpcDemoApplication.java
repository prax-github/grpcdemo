package com.example.grpcdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application for gRPC Demo
 * 
 * This application demonstrates various gRPC concepts:
 * 1. Unary RPC (single request/response)
 * 2. Server streaming RPC
 * 3. Client streaming RPC
 * 4. Bidirectional streaming RPC
 */
@SpringBootApplication
public class GrpcDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrpcDemoApplication.class, args);
    }
}
