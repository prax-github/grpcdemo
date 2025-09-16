package com.example.grpcdemo.config;

import com.example.grpcdemo.service.UserServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;
import java.io.IOException;

/**
 * Configuration class for gRPC server
 * 
 * This class sets up the gRPC server with the UserService implementation
 * and configures it to run on a specified port.
 */
@Configuration
public class GrpcServerConfig {

    @Value("${grpc.server.port:9090}")
    private int grpcPort;

    private Server grpcServer;

    @Bean
    public Server grpcServer(UserServiceImpl userService) throws IOException {
        grpcServer = ServerBuilder.forPort(grpcPort)
                .addService(userService)
                .build()
                .start();

        System.out.println("gRPC server started on port: " + grpcPort);
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC server...");
            grpcServer.shutdown();
        }));

        return grpcServer;
    }

    @PreDestroy
    public void stopGrpcServer() {
        if (grpcServer != null && !grpcServer.isShutdown()) {
            grpcServer.shutdown();
        }
    }
}
