
# Java ReentrantLock Demo

A comprehensive Spring Boot application demonstrating the practical usage of Java's `ReentrantLock` for thread synchronization and concurrent programming. This project showcases real-world scenarios where reentrant locks provide better control over thread synchronization compared to traditional `synchronized` blocks.

## 🎯 Project Overview

This application simulates a **Device Management System** where multiple threads compete for limited license resources while managing device operations. It demonstrates:

- **ReentrantLock** implementation for thread-safe operations
- **Fair vs Unfair** locking mechanisms
- **Condition variables** for advanced thread coordination
- **Thread-safe device management** patterns
- **REST API endpoints** for testing concurrent scenarios

## 🚀 Features

- ✅ Thread-safe device operations (add, enable, disable, delete)
- ✅ Real-time license usage tracking
- ✅ Concurrent user simulation
- ✅ RESTful API for testing lock behavior
- ✅ Comprehensive logging for debugging

## 🛠️ Technology Stack

- **Java 17**
- **Spring Boot 3.5.6**
- **Maven** for dependency management
- **Lombok** for reducing boilerplate code
- **SLF4J** for logging
- **Thales Sentinel SDK** for hardware fingerprinting

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

## 🏃‍♂️ Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/java-reentrant-lock-demo.git
cd java-reentrant-lock-demo
```

### 2. Build and Run
```bash
mvn clean install
mvn spring-boot:run
```

Run unit tests:
```bash
mvn test
```

Run integration tests:
```bash
mvn verify
```


📊 Complete API Reference Table
| Category | Method | Endpoint | Description | Parameters |
|----------|--------|----------|-------------|------------|
| **Device Management** | POST | `/devices/add` | Add new devices | `user` (query), device array (body) |
| | POST | `/devices/enable` | Enable devices (consume licenses) | `user` (query), device IDs array (body) |
| | POST | `/devices/disable` | Disable devices (release licenses) | `user` (query), device IDs array (body) |
| | POST | `/devices/delete` | Delete devices | `user` (query), device IDs array (body) |
| | GET | `/devices/status` | Get all devices | None |
| | GET | `/devices/statusSummary` | Get device status summary | None |
| **Counter Demo** | GET | `/api/counter/unsafe-test` | Test without ReentrantLock | `threads` (optional, default=10) |
| | GET | `/api/counter/safe-test` | Test with ReentrantLock | `threads` (optional, default=10) |
| | GET | `/api/counter/status` | Get counter status | None |

## 📝 Notes

- The application uses a fair lock by default to ensure all waiting threads get equal opportunity
- Device management follows a first-in-first-out approach
- All operations are thread-safe and handle race conditions properly
- Logging provides detailed information about lock acquisition and release events

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/example/reentrantlock/
│   │   ├── ReentrantLockDemoApplication.java
│   │   ├── controller/
│   │   │   └── DeviceController.java
            └── CounterController.java
│   │   ├── service/
│   │   │   └── DeviceService.java
            └── LicenseService.java
            └── CounterService.java
│   │   ├── model/
│   │   │   └── Device.java
│   │   └── config/
│   │       └── DataInitializer.java
│   └── resources/
│       ├── application.properties
│       └── static/
└── test/
    └── java/com/example/reentrantlock/
        ├── ReentrantLockDemoApplicationTests.java
```

## 🔒 Security Considerations

- Hardware-based UUID generation ensures unique identification
- Thread-safe operations prevent data corruption
- Proper lock management prevents deadlocks
- All sensitive operations are logged for audit purposes

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 🙏 Acknowledgments

- Thanks to the Spring Boot team for excellent documentation
- Inspired by real-world concurrent programming challenges
