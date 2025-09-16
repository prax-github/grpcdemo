package com.example.grpcdemo.client;

import com.example.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * gRPC Client for testing all UserService operations
 * 
 * This client demonstrates how to interact with all four types of gRPC calls:
 * 1. Unary RPC
 * 2. Server Streaming RPC
 * 3. Client Streaming RPC
 * 4. Bidirectional Streaming RPC
 */
public class GrpcClient {

    private final ManagedChannel channel;
    private final UserServiceGrpc.UserServiceBlockingStub blockingStub;
    private final UserServiceGrpc.UserServiceStub asyncStub;

    public GrpcClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext() // For demo purposes only - use TLS in production
                .build();
        this.blockingStub = UserServiceGrpc.newBlockingStub(channel);
        this.asyncStub = UserServiceGrpc.newStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /**
     * Test Unary RPC operations
     */
    public void testUnaryRpc() {
        System.out.println("\n=== Testing Unary RPC Operations ===");

        // Create a user
        CreateUserRequest createRequest = CreateUserRequest.newBuilder()
                .setName("Alice Johnson")
                .setEmail("alice.johnson@example.com")
                .setAge(25)
                .setDepartment("Engineering")
                .build();

        CreateUserResponse createResponse = blockingStub.createUser(createRequest);
        System.out.println("Created user: " + createResponse.getUser().getName() + 
                          " with ID: " + createResponse.getUser().getId());

        // Get the user
        GetUserRequest getRequest = GetUserRequest.newBuilder()
                .setId(createResponse.getUser().getId())
                .build();

        GetUserResponse getResponse = blockingStub.getUser(getRequest);
        System.out.println("Retrieved user: " + getResponse.getUser().getName());

        // Update the user
        UpdateUserRequest updateRequest = UpdateUserRequest.newBuilder()
                .setId(createResponse.getUser().getId())
                .setName("Alice Johnson-Smith")
                .setEmail("alice.smith@example.com")
                .setAge(26)
                .setDepartment("Engineering")
                .setStatus(UserStatus.ACTIVE)
                .build();

        UpdateUserResponse updateResponse = blockingStub.updateUser(updateRequest);
        System.out.println("Updated user: " + updateResponse.getUser().getName());
    }

    /**
     * Test Server Streaming RPC
     */
    public void testServerStreaming() throws InterruptedException {
        System.out.println("\n=== Testing Server Streaming RPC ===");

        ListUsersRequest request = ListUsersRequest.newBuilder()
                .setPage(1)
                .setPageSize(10)
                .setDepartment("Engineering") // Filter by department
                .build();

        System.out.println("Streaming users from Engineering department:");
        blockingStub.listUsers(request).forEachRemaining(user -> {
            System.out.println("  - " + user.getName() + " (" + user.getEmail() + ")");
        });
    }

    /**
     * Test Client Streaming RPC
     */
    public void testClientStreaming() throws InterruptedException {
        System.out.println("\n=== Testing Client Streaming RPC ===");

        CountDownLatch finishLatch = new CountDownLatch(1);

        StreamObserver<CreateUserRequest> requestObserver = asyncStub.createMultipleUsers(
                new StreamObserver<CreateUserResponse>() {
                    @Override
                    public void onNext(CreateUserResponse response) {
                        System.out.println("Batch creation result: " + response.getMessage());
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.err.println("Error in client streaming: " + t.getMessage());
                        finishLatch.countDown();
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Client streaming completed");
                        finishLatch.countDown();
                    }
                });

        // Send multiple user creation requests
        String[] names = {"Bob Wilson", "Carol Davis", "David Brown"};
        String[] emails = {"bob.wilson@example.com", "carol.davis@example.com", "david.brown@example.com"};
        String[] departments = {"Marketing", "Sales", "HR"};

        for (int i = 0; i < names.length; i++) {
            CreateUserRequest request = CreateUserRequest.newBuilder()
                    .setName(names[i])
                    .setEmail(emails[i])
                    .setAge(30 + i)
                    .setDepartment(departments[i])
                    .build();

            requestObserver.onNext(request);
            System.out.println("Sent user creation request: " + names[i]);
            Thread.sleep(500); // Simulate some delay
        }

        requestObserver.onCompleted();
        finishLatch.await(10, TimeUnit.SECONDS);
    }

    /**
     * Test Bidirectional Streaming RPC
     */
    public void testBidirectionalStreaming() throws InterruptedException {
        System.out.println("\n=== Testing Bidirectional Streaming RPC ===");

        CountDownLatch finishLatch = new CountDownLatch(1);

        StreamObserver<GetUserRequest> requestObserver = asyncStub.chatWithUsers(
                new StreamObserver<GetUserResponse>() {
                    @Override
                    public void onNext(GetUserResponse response) {
                        User user = response.getUser();
                        System.out.println("Received user info: " + user.getName() + 
                                          " from " + user.getDepartment());
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.err.println("Error in bidirectional streaming: " + t.getMessage());
                        finishLatch.countDown();
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Bidirectional streaming completed");
                        finishLatch.countDown();
                    }
                });

        // Send multiple user lookup requests
        int[] userIds = {1, 2, 3, 4, 5}; // Some may not exist, that's okay for demo

        for (int userId : userIds) {
            GetUserRequest request = GetUserRequest.newBuilder()
                    .setId(userId)
                    .build();

            requestObserver.onNext(request);
            System.out.println("Requested user info for ID: " + userId);
            Thread.sleep(300);
        }

        requestObserver.onCompleted();
        finishLatch.await(10, TimeUnit.SECONDS);
    }

    /**
     * Run all tests
     */
    public void runAllTests() throws InterruptedException {
        System.out.println("Starting gRPC Client Tests...");
        System.out.println("=====================================");

        try {
            testUnaryRpc();
            testServerStreaming();
            testClientStreaming();
            testBidirectionalStreaming();

            System.out.println("\n=====================================");
            System.out.println("All tests completed successfully!");
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        GrpcClient client = new GrpcClient("localhost", 9090);
        client.runAllTests();
    }
}
