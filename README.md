# Real Estate Professionals - Property Listing Service

A full-stack real estate property search and listing application built with React, Spring Boot, and PostgreSQL.

## 📋 Project Overview

Real Estate Professionals is a comprehensive property listing service that allows users to search, filter, and discover real estate properties from multiple MLS data feeds. The application features intelligent relevance ranking based on price proximity, listing recency, property features, and keyword matching.

**Key Features:**
- 🔍 Advanced search filtering by price, bedrooms, location, and keywords
- ⭐ Intelligent relevance ranking algorithm
- 📄 Pagination for browsing large result sets
- 🎨 Clean, responsive React UI
- 🚀 RESTful API with comprehensive error handling
- 🧪 85%+ test coverage (JUnit 5 + Jest)
- 🐳 Docker containerization for local and production deployment
- 🌍 AWS Terraform infrastructure for scalable deployment
- 📊 Test data seeding with realistic property data

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Frontend (React)                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │ SearchForm   │  │ ResultsList  │  │ Pagination   │       │
│  └──────────────┘  └──────────────┘  └──────────────┘       │
└──────────────────────────────────────────────────────────────┘
                            ↓
                     REST API (localhost:8080)
┌──────────────────────────────────────────────────────────────┐
│                  Backend (Spring Boot)                       │
│  ┌────────────┐  ┌──────────┐  ┌────────────────┐            │
│  │ Controller │→ │ Service  │→ │ RankingEngine  │            │
│  └────────────┘  └──────────┘  └────────────────┘            │
│  ┌─────────────────────────────────────────┐                 │
│  │           JPA Repository                │                 │
│  └─────────────────────────────────────────┘                 │
└──────────────────────────────────────────────────────────────┘
                            ↓
┌──────────────────────────────────────────────────────────────┐
│              PostgreSQL Database (5432)                       │
│  ┌─────────────────────────────────────┐                      │
│  │         Listings Table              │                      │
│  │  (id, address, price, bedrooms...)  │                      │
│  └─────────────────────────────────────┘                      │
└──────────────────────────────────────────────────────────────┘
```

## 🚀 Quick Start

### Prerequisites
- **macOS/Linux**: Docker Desktop, Git
- **Java 21** (for local development)
- **Node.js 18+** (for local frontend development)
- **Maven 3.8+** (for Java build)

### Local Development (Docker Compose)

1. **Clone and navigate to project:**
   ```bash
   cd ~/personal/RealStateProfessionals
   ```

2. **Start services:**
   ```bash
   docker-compose up --build
   ```

3. **Access the application:**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - Health Check: http://localhost:8080/api/v1/health

4. **Stop services:**
   ```bash
   docker-compose down
   ```

### Local Development (Without Docker)

**Backend:**
```bash
cd backend
mvn clean install
mvn spring-boot:run
# Runs on http://localhost:8080
```

**Frontend (separate terminal):**
```bash
cd frontend
npm install
npm start
# Opens http://localhost:3000
```

**Database Setup:**
```bash
# Ensure PostgreSQL 15+ is running locally
psql -U postgres -c "CREATE DATABASE realestate;"
```

## 📊 API Specification

### Search Listings
**Endpoint:** `POST /api/v1/listings/search`

**Request:**
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

**Response:**
```json
{
  "success": true,
  "data": {
    "listings": [
      {
        "id": "uuid",
        "address": "123 Main St",
        "city": "Springfield",
        "state": "VA",
        "price": 450000,
        "bedrooms": 2,
        "bathrooms": 1.5,
        "sqft": 980,
        "relevanceScore": 95,
        "status": "active",
        "description": "Bright top-floor condo near shops and transit."
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

### Get Listing Details
**Endpoint:** `GET /api/v1/listings/{id}`

### Health Check
**Endpoint:** `GET /api/v1/health`

## 🧪 Testing

### Backend Tests (JUnit 5)
```bash
cd backend
mvn test
mvn test jacoco:report  # Generate coverage report
# Open: target/site/jacoco/index.html
```

**Test Coverage:**
- SearchService: Filtering, ranking, edge cases
- ListingRepository: Database queries, pagination
- ListingController: API endpoints, error responses
- RankingService: Score calculation algorithms

### Frontend Tests (Jest)
```bash
cd frontend
npm test                 # Run tests
npm run test:coverage   # Generate coverage report
# Open: coverage/lcov-report/index.html
```

**Test Coverage:**
- SearchForm: Validation, submission
- ResultsList: Rendering, empty states
- Pagination: Navigation, boundary conditions
- API service: HTTP requests, error handling

## 📊 Relevance Scoring Algorithm

The search results are ranked using a weighted scoring formula:

```
Score = (0.40 × Price Score) + (0.30 × Recency Score) + 
         (0.20 × Bedroom Score) + (0.10 × Keyword Score)

Scale: 0-100
```

**Components:**
- **Price Score (40%)**: Inverse distance from targetBudget (higher weight for budget proximity)
- **Recency Score (30%)**: Days since listing (newer listings ranked higher)
- **Bedroom Score (20%)**: Match to requested bedroom count
- **Keyword Score (10%)**: Text match in description (full match = 1.0, partial = 0.5, none = 0.0)

See [backend/SCORING_ALGORITHM.md](backend/SCORING_ALGORITHM.md) for detailed implementation.

## 🐳 Docker & Deployment

### Local Development
```bash
docker-compose up --build
```

### Production Deployment (AWS)

**Prerequisites:**
- AWS CLI configured
- Terraform installed
- AWS account with appropriate permissions

**Deploy:**
```bash
cd terraform
terraform init
terraform plan -out=tfplan
terraform apply tfplan
```

**Outputs:**
```bash
terraform output
# Shows: API endpoint, Frontend URL, RDS endpoint
```

See [terraform/README.md](terraform/README.md) for detailed deployment guide.

## 📁 Project Structure

```
RealStateProfessionals/
├── backend/                    # Spring Boot application
│   ├── src/main/java/
│   ├── src/test/java/
│   ├── pom.xml
│   ├── Dockerfile
│   └── README.md
├── frontend/                   # React application
│   ├── src/
│   │   ├── components/
│   │   ├── services/
│   │   ├── hooks/
│   │   └── __tests__/
│   ├── package.json
│   ├── Dockerfile
│   └── README.md
├── terraform/                  # AWS Infrastructure
│   ├── main.tf
│   ├── variables.tf
│   ├── outputs.tf
│   ├── rds.tf
│   ├── ec2.tf
│   └── README.md
├── docker-compose.yml
├── .gitignore
└── README.md
```

## 🔧 Configuration

### Environment Variables

**Backend (.env):**
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/realestate
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

**Frontend (.env):**
```env
REACT_APP_API_URL=http://localhost:8080
```

**Terraform (terraform.tfvars):**
```hcl
aws_region     = "us-east-1"
environment    = "dev"
instance_type  = "t3.medium"
db_instance_class = "db.t3.micro"
```

## 📈 Scalability Considerations

This architecture is designed for scalability:

1. **Database**: PostgreSQL with indexed queries (city, price, bedrooms)
2. **API**: Stateless Spring Boot services, horizontally scalable
3. **Frontend**: Static React build, CDN-ready
4. **Caching**: Ready for Redis integration for frequent queries
5. **Infrastructure**: Terraform supports multi-AZ RDS, auto-scaling EC2 groups

## 🧪 Test Data

The application comes with sample listings data (12 properties across 5 Virginia cities). Data includes:
- Multiple MLS sources for duplicate detection testing
- Various price ranges ($399K - $610K)
- Diverse bedroom counts (2-4 bedrooms)
- Mix of statuses (active, pending, sold)

**Seeding Test Data:**
```bash
# Backend automatically seeds data on startup
curl http://localhost:8080/api/v1/listings/search \
  -H "Content-Type: application/json" \
  -d '{"pageNumber": 1, "pageSize": 20}'
```

## 🤖 AI Assistance

This project was developed with assistance from Claude AI. All code can be explained, modified, and extended. See [CLAUDE_NOTES.md](CLAUDE_NOTES.md) for architecture decisions and trade-offs.

## 📝 License

Private project for interview assessment.

## 👤 Author

Humberto Solano  
Email: humbertoss25@gmail.com

## 🔗 Repository

- **GitHub**: [github.com/yourusername/RealStateProfessionals](https://github.com/yourusername/RealStateProfessionals)
- **Local**: ~/personal/RealStateProfessionals

## 📞 Support

For questions about this implementation:
1. Check [IMPLEMENTATION_STRATEGY.md](IMPLEMENTATION_STRATEGY.md)
2. Review individual README files in backend/, frontend/, terraform/
3. Inspect test files for usage examples

---

**Last Updated**: October 1, 2026  
**Status**: Initial Implementation
