# Bank Transaction Mock Demo

A simple Spring Boot application for managing bank transactions.

## Features

- Create transactions
- Get transactions by reference
- Get all transactions by pagination
- Update transactions
- Delete transactions
- In-memory H2 database for data storage
- Local caching for improved performance
- Exception handling
- RESTful API design
- Swagger documentation
- Docker support

## Prerequisites

- Java 17
- Maven 3.6+
- Docker (optional)


# Build the application
## Clone the repository
git clone https://github.com/15663637602/hsbc.git
cd hsbc

mvn clean package -DskipTests

## Running the Application
### Using Java
java -jar target/transaction-management-0.0.1-SNAPSHOT.jar

# Build the Docker image
docker build -t transaction-management:latest .

# Run the container
docker run -p 8080:8080 transaction-management:latest

# Accessing the Application
- Swagger UI for API documentation: http://localhost:8080/swagger-ui/index.html
- H2 Database Console: http://localhost:8080/h2-console
  - First set spring.h2.console.settings.web-allow-others=true
  - Then input JDBC URL: jdbc:h2:mem:bankdb
