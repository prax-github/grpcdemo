# gRPC Demo - Troubleshooting Guide

## Common Issues and Solutions

### 1. Maven Compilation Errors

**Problem**: `mvn compile` fails with protobuf plugin errors

**Solutions**:
```bash
# Option 1: Clean and rebuild
mvn clean compile

# Option 2: Skip protobuf generation if classes already exist
mvn compile -Dprotobuf.skip=true

# Option 3: Use different protobuf plugin version
# Update pom.xml with version 0.5.1 instead of 0.6.1
```

### 2. Missing Dependencies

**Problem**: `javax.annotation.PreDestroy cannot be resolved`

**Solution**: Already fixed in pom.xml with:
```xml
<dependency>
    <groupId>jakarta.annotation</groupId>
    <artifactId>jakarta.annotation-api</artifactId>
</dependency>
```

### 3. Generated Code Issues

**Problem**: Warnings in generated protobuf classes

**Solution**: These are normal and can be ignored. They don't affect functionality.

### 4. Alternative Setup (If Maven Issues Persist)

**Option A: Use Gradle instead of Maven**
```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.0'
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter'
    implementation 'io.grpc:grpc-netty-shaded:1.58.0'
    implementation 'io.grpc:grpc-protobuf:1.58.0'
    implementation 'io.grpc:grpc-stub:1.58.0'
    implementation 'com.google.protobuf:protobuf-java:3.24.4'
}
```

**Option B: Manual protobuf compilation**
```bash
# Install protoc compiler
# Download from: https://github.com/protocolbuffers/protobuf/releases

# Generate Java classes
protoc --java_out=src/main/java --grpc-java_out=src/main/java src/main/proto/user_service.proto
```

### 5. Quick Test Without Full Setup

Run the simple demo:
```bash
java SimpleGrpcDemo.java
```

This shows all the gRPC concepts without requiring the full Maven setup.

## Working Project Structure

The project should have these key files:
```
├── pom.xml                          # Maven configuration
├── src/main/proto/user_service.proto # Protocol buffer definition
├── src/main/java/com/example/grpcdemo/
│   ├── GrpcDemoApplication.java     # Spring Boot main class
│   ├── config/GrpcServerConfig.java # gRPC server setup
│   ├── service/UserServiceImpl.java # gRPC service implementation
│   └── client/GrpcClient.java       # gRPC client for testing
└── README.md                        # Comprehensive documentation
```

## Key Learning Points (Even Without Running)

1. **Protocol Buffers**: Define data structures and services
2. **gRPC Service**: Four communication patterns
3. **Code Generation**: Automatic client/server code
4. **Spring Integration**: Easy server setup
5. **Error Handling**: gRPC status codes
6. **Streaming**: Real-time data processing

## Interview Preparation

Even if you can't run the full demo, you can still discuss:

- **gRPC Architecture**: HTTP/2, Protocol Buffers, code generation
- **Communication Patterns**: When to use each type
- **Performance Benefits**: Binary serialization, HTTP/2 multiplexing
- **Use Cases**: Microservices, real-time applications
- **Comparison with REST**: Pros and cons of each approach

The code structure and README provide all the concepts you need for interviews!
