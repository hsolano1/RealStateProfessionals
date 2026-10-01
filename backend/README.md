# Real Estate Professionals - Backend API

Spring Boot REST API for property listing search service.

## Overview

Provides a RESTful API for searching, filtering, and retrieving real estate property listings with intelligent relevance ranking.

## Technology Stack

- **Java 21** - Latest LTS JDK
- **Spring Boot 3.3.5** - Modern web framework
- **Spring Data JPA** - ORM and database access
- **PostgreSQL 15** - Relational database
- **Maven 3.8+** - Dependency management
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking library
- **TestContainers** - Integration testing with real databases

## Build & Run

### Prerequisites

- Java 21 JDK
- Maven 3.8+
- PostgreSQL 15+ running on localhost:5432
- Or use Docker Compose from project root

### Build

```bash
mvn clean install
```

### Run Locally

```bash
# Create database first
psql -U postgres -c "CREATE DATABASE realestate;"

# Run application
mvn spring-boot:run
```

### Run with Docker Compose

From project root:
```bash
docker-compose up backend
```

Access API at `http://localhost:8080`

## API Endpoints

### Search Listings
**POST** `/api/v1/listings/search`

Request:
```json
{
  "minPrice": 300000,
  "maxPrice": 600000,
  "minBedrooms": 2,
  "city": "Springfield",
  "keyword": "pet friendly",
  "targetBudget": 450000,
  "pageNumber": 1,
  "pageSize": 10
}
```

Response:
```json
{
  "success": true,
  "data": {
    "listings": [
      {
        "id": "uuid",
        "address": "123 Main St",
        "city": "Springfield",
        "price": 450000,
        "bedrooms": 2,
        "bathrooms": 1.5,
        "relevanceScore": 95,
        "status": "active"
      }
    ],
    "totalCount": 25,
    "pageNumber": 1,
    "pageSize": 10,
    "totalPages": 3
  },
  "timestamp": "2026-10-01T12:30:00Z"
}
```

### Get Listing by ID
**GET** `/api/v1/listings/{id}`

### Health Check
**GET** `/api/v1/health`

## Project Structure

```
backend/
├── src/main/java/com/realestate/
│   ├── RealStateApplication.java
│   ├── config/           # Configuration classes
│   ├── controller/       # REST controllers
│   ├── entity/          # JPA entities
│   ├── dto/             # Data transfer objects
│   ├── repository/      # Data access layer
│   ├── service/         # Business logic
│   └── util/            # Utility classes
├── src/test/java/       # Test classes
├── src/main/resources/
│   ├── application.properties
│   └── sql/             # SQL scripts
├── pom.xml
└── Dockerfile
```

## Testing

### Run All Tests
```bash
mvn test
```

### Run Tests with Coverage Report
```bash
mvn test jacoco:report
# Open: target/site/jacoco/index.html
```

### Test Categories

1. **Service Tests** - Business logic validation
2. **Repository Tests** - Database query testing with TestContainers
3. **Controller Tests** - API endpoint validation
4. **Ranking Tests** - Scoring algorithm verification

### Key Test Scenarios

- Empty search results
- Tied scores
- Pagination boundaries
- Invalid filter combinations
- Price range validation
- Bedroom matching
- Keyword matching

## Relevance Scoring Algorithm

### Formula
```
Score = (0.40 × Price Score) + (0.30 × Recency Score) + 
         (0.20 × Bedroom Score) + (0.10 × Keyword Score)

Scale: 0-100
```

### Components

**Price Score (40% weight)**
- Measures proximity to target budget
- Higher score for prices close to target
- Formula: `1.0 - abs(listingPrice - targetBudget) / targetBudget`

**Recency Score (30% weight)**
- Newer listings ranked higher
- Days since listing / max_days (365)
- More recent = higher score

**Bedroom Score (20% weight)**
- Exact match = 1.0
- More than requested = 0.9 - ((difference) × 0.1)
- Less than requested = 0.85 - ((difference) × 0.15)

**Keyword Score (10% weight)**
- Full match in description = 1.0
- Partial match / word match = 0.5-0.7
- No match = 0.0

## Configuration

### Application Properties

**Database:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/realestate
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
```

**Logging:**
```properties
logging.level.root=INFO
logging.level.com.realestate=DEBUG
```

**Validation:**
- minPrice ≤ maxPrice
- minBedrooms ≥ 0
- pageSize > 0 and ≤ 1000
- targetBudget required and > 0

## Database Schema

### Listings Table
```sql
CREATE TABLE listings (
  id UUID PRIMARY KEY,
  source_id VARCHAR(50) NOT NULL,
  source_system VARCHAR(20) NOT NULL,
  address VARCHAR(255) NOT NULL,
  city VARCHAR(100) NOT NULL,
  state VARCHAR(2) NOT NULL,
  zip VARCHAR(10) NOT NULL,
  price DECIMAL(15,2) NOT NULL,
  bedrooms INTEGER NOT NULL,
  bathrooms DECIMAL(3,1) NOT NULL,
  sqft INTEGER NOT NULL,
  latitude DECIMAL(9,6),
  longitude DECIMAL(9,6),
  listed_date DATE NOT NULL,
  status VARCHAR(20) NOT NULL,
  description TEXT,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,
  UNIQUE(source_id, source_system)
);

CREATE INDEX idx_city ON listings(city);
CREATE INDEX idx_price ON listings(price);
CREATE INDEX idx_bedrooms ON listings(bedrooms);
CREATE INDEX idx_status ON listings(status);
```

## Performance Considerations

1. **Database Indexing** - Indexed on frequently queried columns
2. **Pagination** - Limit result sets to 1000 per page
3. **Query Optimization** - Uses JPA query hints and batch processing
4. **Caching Ready** - Structure supports future Redis integration

## Error Handling

### HTTP Status Codes
- `200 OK` - Successful search (even if no results)
- `400 Bad Request` - Invalid input parameters
- `404 Not Found` - Listing not found
- `500 Internal Server Error` - Unexpected server error

### Error Responses
```json
{
  "success": false,
  "errorMessage": "Min price cannot be greater than max price",
  "timestamp": "2026-10-01T12:30:00Z"
}
```

## Deployment

### Docker Build
```bash
docker build -t realestate-backend:latest .
docker run -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/realestate realestate-backend:latest
```

### Environment Variables
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/realestate
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

## Monitoring & Logging

### Logging Levels
- `DEBUG` - Application-specific logs
- `INFO` - General application information
- `WARN` - Warning messages
- `ERROR` - Error conditions

### CloudWatch Integration
Logs are exported to CloudWatch for monitoring and debugging.

## Future Enhancements

1. Caching layer (Redis)
2. Advanced search filters (geospatial, date range)
3. User authentication and authorization
4. Saved searches and favorites
5. Real-time notifications
6. Analytics dashboard
7. API rate limiting
8. Swagger/OpenAPI documentation

## License

Private project for interview assessment.

## Support

For issues or questions, refer to the main README.md in the project root.
