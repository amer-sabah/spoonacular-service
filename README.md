# Spoonacular Service

A Spring Boot RESTful backend application for the Spoonacular service.

## Prerequisites

- Java 21 (LTS)
- Maven 3.6 or higher
- Spoonacular API Clients library (must be installed locally)

## Getting Started

### Install Spoonacular API Clients Dependency

Before building the application, you need to install the `spoonacular-api-clients` dependency locally:

1. Clone or download the spoonacular-api-clients library from [https://github.com/ddsky/spoonacular-api-clients](https://github.com/ddsky/spoonacular-api-clients)
2. Navigate to the spoonacular-api-clients directory
3. Run Maven install:

```bash
cd path/to/spoonacular-api-clients
mvn clean install
```

This will install the library to your local Maven repository (`~/.m2/repository`).

### Configure Spoonacular API Key

The application requires a valid Spoonacular API key to function. You need to configure it in the `application.properties` file:

1. Open `src/main/resources/application.properties`
2. Update the API key with your own:

```properties
spoonacular.api.key=your-api-key-here
```

To get a Spoonacular API key:
1. Visit [https://spoonacular.com/food-api](https://spoonacular.com/food-api)
2. Sign up for a free account
3. Copy your API key from the dashboard
4. Replace `your-api-key-here` in the configuration file

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

## Features

### Response Caching

The application implements a JSON file-based caching system to optimize API usage and improve performance:

- **Automatic caching** of recipe search results, recipe details, and ingredient information
- **24-hour cache duration** for all responses
- **100 entries maximum** per cache type with automatic cleanup
- **Reduced API quota usage** by serving cached responses when available

For detailed information about the caching implementation, see [CACHE_README.md](CACHE_README.md).

## API Endpoints

The application provides the following REST API endpoints:

### 1. Recipe Search Endpoint

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

### 2. Recipe Information Endpoint

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

### 3. Ingredient Information Endpoint

- **URL:** `/ingredients/{id}`
- **Method:** `GET`
- **Path Parameters:**
  - `id` (required): Ingredient ID
- **Query Parameters:**
  - `amount` (optional): The amount of the ingredient
  - `unit` (optional): The unit of measurement (e.g., "grams", "cups", "oz")

#### Example Requests

**Basic ingredient information:**
```bash
curl "http://localhost:8080/ingredients/9266"
```

**Ingredient information with amount and unit:**
```bash
curl "http://localhost:8080/ingredients/9266?amount=100&unit=grams"
```

## Project Structure

```
SpoonacularBackend/
├── pom.xml
├── README.md
├── CACHE_README.md
├── cache/
│   ├── ingredients/
│   │   └── info/
│   └── recipes/
│       ├── info/
│       └── search/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── wiley/
│   │   │           └── spoonacular/
│   │   │               ├── SpoonacularServiceApplication.java
│   │   │               ├── cache/
│   │   │               │   └── JsonFileCache.java
│   │   │               ├── config/
│   │   │               │   └── CorsConfig.java
│   │   │               ├── controller/
│   │   │               │   ├── IngredientsServiceController.java
│   │   │               │   ├── RecipesServiceController.java
│   │   │               │   └── SpoonacularServiceController.java
│   │   │               └── service/
│   │   │                   ├── IngredientsApiService.java
│   │   │                   └── RecipesApiService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── wiley/
│                   └── spoonacular/
│                       ├── cache/
│                       │   └── JsonFileCacheTest.java
│                       └── service/
│                           ├── IngredientsApiServiceTest.java
│                           └── RecipesApiServiceTest.java
└── target/
    ├── classes/
    ├── test-classes/
    └── surefire-reports/
```

## Technologies Used

- Spring Boot 3.2.0
- Java 21
- Maven
