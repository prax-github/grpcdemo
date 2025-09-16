@echo off
echo Starting gRPC Demo...
echo.

echo Step 1: Building the project...
call mvn clean compile
if %errorlevel% neq 0 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo Step 2: Starting gRPC Server...
echo The server will start on port 9090 (gRPC) and 8080 (Spring Boot)
echo Press Ctrl+C to stop the server
echo.
start "gRPC Server" cmd /k "mvn spring-boot:run"

echo.
echo Step 3: Waiting for server to start...
timeout /t 10 /nobreak > nul

echo.
echo Step 4: Running gRPC Client Tests...
echo This will demonstrate all four gRPC communication patterns
echo.
call mvn exec:java -Dexec.mainClass="com.example.grpcdemo.client.GrpcClient"

echo.
echo Demo completed! Check the server window for server-side logs.
echo.
pause
