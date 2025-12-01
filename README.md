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

### Recipe Search Endpoint

- **URL:** `/recipes/search`
- **Method:** `GET`
- **Query Parameters:**
  - `query` (required): Search term (e.g., "pasta", "chicken", "salad")
  - `maxResultSize` (optional): Maximum number of results (default: 10, max: 100)
  - `cuisines` (optional): List of cuisines to filter by (comma-separated)
  - `maxCalories` (optional): Maximum calories per serving

#### Supported Cuisines

African, Asian, American, British, Cajun, Caribbean, Chinese, Eastern European, European, French, German, Greek, Indian, Irish, Italian, Japanese, Jewish, Korean, Latin American, Mediterranean, Mexican, Middle Eastern, Nordic, Southern, Spanish, Thai, Vietnamese

#### Example Requests

**Basic search:**
```bash
curl "http://localhost:8080/recipes/search?query=pasta"
```

**Search with result limit:**
```bash
curl "http://localhost:8080/recipes/search?query=pasta&maxResultSize=5"
```

**Search with single cuisine:**
```bash
curl "http://localhost:8080/recipes/search?query=pasta&cuisines=Italian"
```

**Search with multiple cuisines:**
```bash
curl "http://localhost:8080/recipes/search?query=chicken&cuisines=Italian,Mexican,Chinese"
```

**Search with cuisines and result limit:**
```bash
curl "http://localhost:8080/recipes/search?query=rice&cuisines=Asian,Indian&maxResultSize=15"
```

**Search with max calories:**
```bash
curl "http://localhost:8080/recipes/search?query=salad&maxCalories=500"
```

**Search with cuisines and max calories:**
```bash
curl "http://localhost:8080/recipes/search?query=pasta&cuisines=Italian,Mediterranean&maxCalories=600"
```

**Search with all filters:**
```bash
curl "http://localhost:8080/recipes/search?query=chicken&cuisines=Asian,Chinese&maxCalories=700&maxResultSize=20"
```

### Recipe Information Endpoint

- **URL:** `/recipes/{id}`
- **Method:** `GET`
- **Path Parameters:**
  - `id` (required): Recipe ID
- **Query Parameters:**
  - `includeNutrition` (optional): Whether to include nutrition data (default: false)

#### Example Request

```bash
curl "http://localhost:8080/recipes/715538?includeNutrition=true"
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
