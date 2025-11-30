# Spoonacular Service

A Spring Boot RESTful backend application for the Spoonacular service.

## Prerequisites

- Java 21 (LTS)
- Maven 3.6 or higher

## Getting Started

### Build the Application

```bash
mvn clean install
```

### Run the Application

```bash
mvn spring-boot:run
```

Or run the packaged JAR file:

```bash
java -jar target/spoonacular-service-1.0.0.jar
```

The application will start on `http://localhost:8080` (configured in `src/main/resources/application.properties`).

## API Endpoints

### Hello Endpoint

- **URL:** `/api/hello`
- **Method:** `GET`
- **Response:** `Hello, Spoonacular Service!`

### Example Request

```bash
curl http://localhost:8080/api/hello
```

### Example Response

```
Hello, Spoonacular Service!
```

## Project Structure

```
SpoonacularBackend/
├── pom.xml
├── README.md
└── src
    └── main
        ├── java
        │   └── com
        │       └── wiley
        │           └── spoonacular
        │               ├── SpoonacularServiceApplication.java
        │               └── controller
        │                   └── SpoonacularServiceController.java
        └── resources
            └── application.properties
```

## Technologies Used

- Spring Boot 3.2.0
- Java 21
- Maven
