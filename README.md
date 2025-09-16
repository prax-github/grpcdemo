# gRPC Demo - Complete Learning Project

A comprehensive gRPC proof of concept built with Java Spring Boot and Maven, designed to help you understand and speak confidently about gRPC in interviews.

## 🎯 What You'll Learn

This project demonstrates all four types of gRPC communication patterns:

1. **Unary RPC** - Single request, single response (like REST API calls)
2. **Server Streaming RPC** - Single request, multiple responses
3. **Client Streaming RPC** - Multiple requests, single response
4. **Bidirectional Streaming RPC** - Multiple requests, multiple responses

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/example/grpcdemo/
│   │   ├── GrpcDemoApplication.java          # Main Spring Boot app
│   │   ├── config/
│   │   │   └── GrpcServerConfig.java         # gRPC server configuration
│   │   ├── service/
│   │   │   └── UserServiceImpl.java          # gRPC service implementation
│   │   └── client/
│   │       └── GrpcClient.java               # gRPC client for testing
│   └── resources/
│       ├── proto/
│       │   └── user_service.proto            # Protocol Buffer definitions
│       └── application.properties            # Application configuration
└── test/
    └── java/com/example/grpcdemo/            # Test classes
```

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### 1. Build the Project
```bash
mvn clean compile
```

### 2. Run the gRPC Server
```bash
mvn spring-boot:run
```

The server will start on port 9090 (gRPC) and 8080 (Spring Boot).

### 3. Run the Client Tests
In a separate terminal:
```bash
mvn exec:java -Dexec.mainClass="com.example.grpcdemo.client.GrpcClient"
```

## 📚 gRPC Concepts Explained

### What is gRPC?

gRPC (gRPC Remote Procedure Calls) is a high-performance, open-source RPC framework developed by Google. It uses HTTP/2 for transport, Protocol Buffers as the interface description language, and provides features such as authentication, load balancing, and more.

### Key Advantages of gRPC:

1. **Performance**: Uses HTTP/2 and binary serialization (Protocol Buffers)
2. **Language Agnostic**: Works across multiple programming languages
3. **Code Generation**: Automatic client/server code generation
4. **Streaming**: Built-in support for streaming data
5. **Type Safety**: Strong typing with Protocol Buffers
6. **Load Balancing**: Built-in load balancing support

### Protocol Buffers (protobuf)

Protocol Buffers are Google's language-neutral, platform-neutral, extensible mechanism for serializing structured data. They're more efficient than JSON/XML and provide strong typing.

Example from our project:
```protobuf
message User {
  int32 id = 1;
  string name = 2;
  string email = 3;
  int32 age = 4;
  string department = 5;
  UserStatus status = 6;
  int64 created_at = 7;
  int64 updated_at = 8;
}
```

## 🔄 gRPC Communication Patterns

### 1. Unary RPC (Request-Response)
```java
// Client sends one request, server sends one response
CreateUserResponse response = blockingStub.createUser(request);
```
**Use Cases**: CRUD operations, simple queries, authentication

### 2. Server Streaming RPC
```java
// Client sends one request, server sends multiple responses
blockingStub.listUsers(request).forEachRemaining(user -> {
    // Process each user
});
```
**Use Cases**: Large data sets, real-time updates, file downloads

### 3. Client Streaming RPC
```java
// Client sends multiple requests, server sends one response
StreamObserver<CreateUserRequest> requestObserver = 
    asyncStub.createMultipleUsers(responseObserver);
```
**Use Cases**: Batch uploads, data collection, logging

### 4. Bidirectional Streaming RPC
```java
// Both client and server can send multiple messages
StreamObserver<GetUserRequest> requestObserver = 
    asyncStub.chatWithUsers(responseObserver);
```
**Use Cases**: Chat applications, real-time gaming, live data processing

## 🛠️ Implementation Details

### Service Definition
Our `UserService` provides:
- **Unary operations**: Create, Read, Update, Delete users
- **Server streaming**: List users with filtering and pagination
- **Client streaming**: Batch user creation
- **Bidirectional streaming**: Real-time user chat simulation

### Error Handling
gRPC uses status codes for error handling:
- `OK` (0): Success
- `CANCELLED` (1): Operation was cancelled
- `UNKNOWN` (2): Unknown error
- `INVALID_ARGUMENT` (3): Invalid argument
- `NOT_FOUND` (5): Resource not found
- `ALREADY_EXISTS` (6): Resource already exists
- `PERMISSION_DENIED` (7): Permission denied
- `UNAUTHENTICATED` (16): Authentication required

### Data Storage
This demo uses in-memory storage (`ConcurrentHashMap`) for simplicity. In production, you'd typically use:
- Databases (PostgreSQL, MongoDB, etc.)
- Caching layers (Redis, Memcached)
- Message queues (Kafka, RabbitMQ)

## 🧪 Testing the Application

### Manual Testing
1. Start the server: `mvn spring-boot:run`
2. Run the client: `mvn exec:java -Dexec.mainClass="com.example.grpcdemo.client.GrpcClient"`
3. Observe the console output showing all four communication patterns

### Expected Output
```
Starting gRPC Client Tests...
=====================================

=== Testing Unary RPC Operations ===
Created user: Alice Johnson with ID: 3
Retrieved user: Alice Johnson
Updated user: Alice Johnson-Smith

=== Testing Server Streaming RPC ===
Streaming users from Engineering department:
  - John Doe (john.doe@example.com)
  - Alice Johnson-Smith (alice.smith@example.com)

=== Testing Client Streaming RPC ===
Sent user creation request: Bob Wilson
Sent user creation request: Carol Davis
Sent user creation request: David Brown
Batch creation result: Successfully created 3 users in batch

=== Testing Bidirectional Streaming RPC ===
Requested user info for ID: 1
Received user info: John Doe from Engineering
Requested user info for ID: 2
Received user info: Jane Smith from Marketing
...
```

## 🎤 Interview Talking Points

### When asked "What is gRPC?"
- "gRPC is a high-performance RPC framework that uses HTTP/2 and Protocol Buffers"
- "It provides four communication patterns: unary, server streaming, client streaming, and bidirectional streaming"
- "It's language-agnostic and generates client/server code automatically"

### When asked "gRPC vs REST?"
- **Performance**: gRPC uses binary serialization and HTTP/2 multiplexing
- **Streaming**: gRPC has built-in streaming support
- **Type Safety**: gRPC uses strongly-typed Protocol Buffers
- **Browser Support**: REST has better browser support, gRPC needs gRPC-Web
- **Caching**: REST has better HTTP caching support

### When asked "When to use gRPC?"
- Microservices communication
- Real-time applications requiring streaming
- High-performance requirements
- Polyglot environments (multiple languages)
- When you need strong typing and code generation

### When asked "gRPC challenges?"
- Learning curve with Protocol Buffers
- Limited browser support (needs gRPC-Web)
- Debugging can be more complex than REST
- Less tooling compared to REST/JSON

## 🔧 Advanced Topics to Explore

1. **Authentication & Authorization**: JWT tokens, mTLS
2. **Load Balancing**: Client-side and server-side load balancing
3. **Interceptors**: Request/response logging, metrics, tracing
4. **Deadlines & Timeouts**: Request timeout handling
5. **Error Handling**: Custom error types and status codes
6. **gRPC-Web**: Browser client support
7. **Service Mesh**: Istio, Envoy proxy integration

## 📖 Additional Resources

- [gRPC Official Documentation](https://grpc.io/docs/)
- [Protocol Buffers Guide](https://developers.google.com/protocol-buffers)
- [gRPC Best Practices](https://grpc.io/docs/guides/best-practices/)
- [Spring Boot gRPC Integration](https://github.com/LogNet/grpc-spring-boot-starter)

## 🤝 Contributing

Feel free to extend this demo with:
- Database integration
- Authentication mechanisms
- More complex business logic
- Unit and integration tests
- Docker containerization

---

**Happy Learning! 🚀**

This project gives you hands-on experience with all gRPC concepts you'll need for interviews. Practice explaining the code and concepts out loud to build confidence!