package com.example.grpcdemo.service;

import com.example.grpc.*;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Implementation of UserService gRPC service
 * 
 * This service demonstrates all four types of gRPC communication patterns:
 * 1. Unary RPC: CreateUser, GetUser, UpdateUser, DeleteUser
 * 2. Server Streaming: ListUsers
 * 3. Client Streaming: CreateMultipleUsers
 * 4. Bidirectional Streaming: ChatWithUsers
 */
@Service
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    // In-memory storage for demo purposes
    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public UserServiceImpl() {
        // Initialize with some sample data
        initializeSampleData();
    }

    private void initializeSampleData() {
        User user1 = User.newBuilder()
                .setId(1)
                .setName("John Doe")
                .setEmail("john.doe@example.com")
                .setAge(30)
                .setDepartment("Engineering")
                .setStatus(UserStatus.ACTIVE)
                .setCreatedAt(System.currentTimeMillis())
                .setUpdatedAt(System.currentTimeMillis())
                .build();

        User user2 = User.newBuilder()
                .setId(2)
                .setName("Jane Smith")
                .setEmail("jane.smith@example.com")
                .setAge(28)
                .setDepartment("Marketing")
                .setStatus(UserStatus.ACTIVE)
                .setCreatedAt(System.currentTimeMillis())
                .setUpdatedAt(System.currentTimeMillis())
                .build();

        users.put(1, user1);
        users.put(2, user2);
        nextId.set(3);
    }

    /**
     * Unary RPC: Create a single user
     */
    @Override
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
        try {
            int id = nextId.getAndIncrement();
            User user = User.newBuilder()
                    .setId(id)
                    .setName(request.getName())
                    .setEmail(request.getEmail())
                    .setAge(request.getAge())
                    .setDepartment(request.getDepartment())
                    .setStatus(UserStatus.ACTIVE)
                    .setCreatedAt(System.currentTimeMillis())
                    .setUpdatedAt(System.currentTimeMillis())
                    .build();

            users.put(id, user);

            CreateUserResponse response = CreateUserResponse.newBuilder()
                    .setUser(user)
                    .setMessage("User created successfully")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            System.out.println("Created user: " + user.getName() + " with ID: " + id);
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    /**
     * Unary RPC: Get a single user by ID
     */
    @Override
    public void getUser(GetUserRequest request, StreamObserver<GetUserResponse> responseObserver) {
        try {
            User user = users.get(request.getId());
            if (user == null) {
                responseObserver.onError(new RuntimeException("User not found with ID: " + request.getId()));
                return;
            }

            GetUserResponse response = GetUserResponse.newBuilder()
                    .setUser(user)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            System.out.println("Retrieved user: " + user.getName());
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    /**
     * Unary RPC: Update a user
     */
    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<UpdateUserResponse> responseObserver) {
        try {
            User existingUser = users.get(request.getId());
            if (existingUser == null) {
                responseObserver.onError(new RuntimeException("User not found with ID: " + request.getId()));
                return;
            }

            User updatedUser = existingUser.toBuilder()
                    .setName(request.getName())
                    .setEmail(request.getEmail())
                    .setAge(request.getAge())
                    .setDepartment(request.getDepartment())
                    .setStatus(request.getStatus())
                    .setUpdatedAt(System.currentTimeMillis())
                    .build();

            users.put(request.getId(), updatedUser);

            UpdateUserResponse response = UpdateUserResponse.newBuilder()
                    .setUser(updatedUser)
                    .setMessage("User updated successfully")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            System.out.println("Updated user: " + updatedUser.getName());
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    /**
     * Unary RPC: Delete a user
     */
    @Override
    public void deleteUser(DeleteUserRequest request, StreamObserver<DeleteUserResponse> responseObserver) {
        try {
            User user = users.remove(request.getId());
            if (user == null) {
                responseObserver.onError(new RuntimeException("User not found with ID: " + request.getId()));
                return;
            }

            DeleteUserResponse response = DeleteUserResponse.newBuilder()
                    .setMessage("User deleted successfully: " + user.getName())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            System.out.println("Deleted user: " + user.getName());
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    /**
     * Server Streaming RPC: Stream all users
     */
    @Override
    public void listUsers(ListUsersRequest request, StreamObserver<User> responseObserver) {
        try {
            List<User> userList = new ArrayList<>(users.values());
            
            // Apply department filter if specified
            if (!request.getDepartment().isEmpty()) {
                userList = userList.stream()
                        .filter(user -> user.getDepartment().equals(request.getDepartment()))
                        .collect(Collectors.toList());
            }

            // Apply pagination
            int startIndex = (request.getPage() - 1) * request.getPageSize();
            int endIndex = Math.min(startIndex + request.getPageSize(), userList.size());

            for (int i = startIndex; i < endIndex; i++) {
                responseObserver.onNext(userList.get(i));
                // Simulate some processing time
                Thread.sleep(100);
            }

            responseObserver.onCompleted();
            System.out.println("Streamed " + (endIndex - startIndex) + " users");
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    /**
     * Client Streaming RPC: Create multiple users from a stream
     */
    @Override
    public StreamObserver<CreateUserRequest> createMultipleUsers(StreamObserver<CreateUserResponse> responseObserver) {
        return new StreamObserver<CreateUserRequest>() {
            private int createdCount = 0;
            private final List<User> createdUsers = new ArrayList<>();

            @Override
            public void onNext(CreateUserRequest request) {
                try {
                    int id = nextId.getAndIncrement();
                    User user = User.newBuilder()
                            .setId(id)
                            .setName(request.getName())
                            .setEmail(request.getEmail())
                            .setAge(request.getAge())
                            .setDepartment(request.getDepartment())
                            .setStatus(UserStatus.ACTIVE)
                            .setCreatedAt(System.currentTimeMillis())
                            .setUpdatedAt(System.currentTimeMillis())
                            .build();

                    users.put(id, user);
                    createdUsers.add(user);
                    createdCount++;

                    System.out.println("Created user in batch: " + user.getName());
                } catch (Exception e) {
                    responseObserver.onError(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error in client streaming: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                // Send final response with summary
                CreateUserResponse response = CreateUserResponse.newBuilder()
                        .setMessage("Successfully created " + createdCount + " users in batch")
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
                System.out.println("Completed batch creation of " + createdCount + " users");
            }
        };
    }

    /**
     * Bidirectional Streaming RPC: Chat with users
     */
    @Override
    public StreamObserver<GetUserRequest> chatWithUsers(StreamObserver<GetUserResponse> responseObserver) {
        return new StreamObserver<GetUserRequest>() {
            @Override
            public void onNext(GetUserRequest request) {
                try {
                    User user = users.get(request.getId());
                    if (user != null) {
                        GetUserResponse response = GetUserResponse.newBuilder()
                                .setUser(user)
                                .build();
                        responseObserver.onNext(response);
                        System.out.println("Sent user info for chat: " + user.getName());
                    } else {
                        System.out.println("User not found for chat: " + request.getId());
                    }
                } catch (Exception e) {
                    responseObserver.onError(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error in bidirectional streaming: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
                System.out.println("Chat session completed");
            }
        };
    }
}
