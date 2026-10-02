# Real Estate Professionals - Project Context

**Created**: October 2026  
**Purpose**: Real Estate property listing service (Brillio coding assessment)  
**Location**: ~/personal/RealStateProfessionals  

---

## 🎯 Project Status

### ✅ COMPLETE & WORKING
- **Docker deployment** with PostgreSQL 15 — fully functional
- **React 18 frontend** — built and served from backend
- **Spring Boot 3.3.5 backend** with Java 21 — all endpoints working
- **Search API** with filtering, pagination, relevance ranking
- **10 sample properties** pre-loaded in database
- **Terraform scripts** for AWS EC2 + RDS deployment

### ⚠️ PARTIAL (Infrastructure ready, integration incomplete)
- **Standalone mode** (JSON file storage) — FileListingStorage class exists but ListingService doesn't use it yet
- Would need ~15 min refactoring to wire up storage abstraction in ListingService

---

## 🛠️ Tech Stack

| Component | Version | Notes |
|-----------|---------|-------|
| Backend | Spring Boot 3.3.5, Java 21 | Maven build, no Lombok (manual impl) |
| Frontend | React 18, JavaScript | Pre-built, served as static resources |
| Database | PostgreSQL 15 | Docker containerized |
| Build | Maven 3.8+ | Configured for Docker & standalone |
| Testing | JUnit 5, Jest | Configuration in place |

---

## 📁 Critical Files & Their Purpose

### Backend Core
- `backend/src/main/java/com/realestate/service/ListingService.java` — Search logic, filtering, ranking
- `backend/src/main/java/com/realestate/controller/ListingController.java` — REST endpoints (/api/v1/listings/search, /health)
- `backend/src/main/java/com/realestate/service/RankingService.java` — Relevance scoring algorithm (40% price, 30% recency, 20% bedrooms, 10% keyword)
- `backend/src/main/java/com/realestate/entity/Listing.java` — JPA entity, manual getters/setters, no Lombok
- `backend/src/main/java/com/realestate/storage/FileListingStorage.java` — JSON file storage (standalone mode, NOT YET INTEGRATED)

### Configuration
- `backend/src/main/resources/application.properties` — Default config (PostgreSQL)
- `backend/src/main/resources/application-file.properties` — Standalone config (file storage)
- `backend/pom.xml` — Maven dependencies (Lombok completely removed)

### Frontend
- `frontend/src/App.js` — Main search component
- `frontend/build/` — Pre-built production build (included in JAR)

### Deployment
- `docker-compose.yml` — 3 services: postgres, backend, frontend
- `run.sh` / `run.bat` — Standalone Java-only launcher (incomplete integration)
- `terraform/` — AWS infrastructure code

---

## 🔧 How to Run

### Option 1: Docker (Recommended, Fully Working)
```bash
docker compose up
# Access: http://localhost:8080
```
- Uses PostgreSQL (included)
- No additional setup needed
- Perfect for production

### Option 2: Standalone (Java 21 Only) - PARTIAL
```bash
./run.sh  # or run.bat on Windows
```
- Would use JSON file storage
- **STATUS**: Infrastructure exists but ListingService isn't wired to use FileListingStorage yet
- **TO COMPLETE**: Refactor ListingService to inject ListingStorage instead of ListingRepository (15 min work)

---

## 🔑 Key Technical Decisions

### Why No Lombok
- **Problem**: Docker Maven build annotation processor wasn't running
- **Solution**: Removed Lombok entirely, manually implemented all getters/setters/constructors
- **Result**: More verbose but transparent; no build issues

### Why Pre-built Frontend in Backend
- Reduces deployment complexity (single JAR)
- Frontend assets (`frontend/build/`) copied to `backend/src/main/resources/static/`
- Served as static resources via Spring

### Why Manual Filtering in Java (not JPQL)
- PostgreSQL NULL parameter binding had issues with `LOWER()` function
- Moved from complex JPQL to simple `findAll()` + Java stream filtering
- More maintainable and no database type inference issues

### Why File Storage Abstraction Exists
- User requirement: provide standalone option without Docker/PostgreSQL
- FileListingStorage class ready but not yet wired into service layer
- Can be completed quickly if needed

---

## 📊 Search Algorithm (Relevance Ranking)

Weighted formula: `(40% price + 30% recency + 20% bedrooms + 10% keyword) × 100`

- **Price Score**: How close listing price is to targetBudget (0.0-1.0)
- **Recency Score**: How recently listed (1.0 for today, decays over 365 days)
- **Bedroom Score**: Match to minBedrooms preference (1.0 = exact match)
- **Keyword Score**: Text match in description/address (0.0-1.0)

---

## ⚠️ Known Limitations & Fixes Applied

### Recent Fixes (Oct 1, 2026)
1. **Spring WebConfig Pattern Error** — Invalid `"/**/{spring:\w+}"` pattern → Simplified to root "/" only
2. **Missing Filter Logic** — Search wasn't calling `filterListings()` → Added filter chain
3. **Lombok Import Errors** — Removed all Lombok, replaced with explicit constructors
4. **PostgreSQL NULL Binding** — LOWER() function with NULL params → Switched to Java filtering

### Search API Before Fix
- Would return HTTP 500 with generic error
- Root cause: Spring routing pattern broke startup

### Search API After Fix
- Returns filtered results with relevance scoring
- Pagination works correctly
- All filters (city, price, bedrooms, keyword) working

---

## 🚀 Deployment Options Available

### Production (Tested & Working)
- **Docker Compose**: `docker compose up` → full stack with PostgreSQL

### AWS
- Terraform scripts in `terraform/` — EC2 + RDS setup ready (not tested)

### Development Laptop
- **Docker**: Same as production
- **Standalone**: Requires Java 21+ (FileListingStorage integration needed)

---

## 📝 Next Steps (If Continuing)

### To Complete Standalone Mode
1. Modify `ListingService` to accept `ListingStorage` interface instead of `ListingRepository`
2. Use `@ConditionalOnProperty` to select storage implementation (postgres vs file)
3. Test with `run.sh` to verify JSON file creation and search

### To Package for Delivery
```bash
# Create ZIP with both options
zip -r RealEstateProfessionals.zip \
  backend/ frontend/ terraform/ \
  docker-compose.yml run.sh run.bat \
  README.md SETUP_GUIDE.md *.properties
```

### To Deploy to AWS
```bash
cd terraform
terraform init
terraform apply  # (not yet tested)
```

---

## 💾 Data & Sample Properties

- **10 sample properties** in Virginia (Springfield, Reston, Fairfax, Vienna, Manassas, Chantilly)
- Seeded via `DataInitializer` component at startup
- Properties have full details: address, price, bedrooms, bathrooms, description, GPS coords
- Database: PostgreSQL with `listings` table (indexed on city, price, bedrooms, status)

---

## 🧪 Testing

### Manual Testing (Verified Working)
```bash
# Search all (10 results, paginated)
curl -X POST http://localhost:8080/api/v1/listings/search \
  -H "Content-Type: application/json" \
  -d '{"pageNumber": 1, "pageSize": 3}'

# Filter by city
curl -X POST http://localhost:8080/api/v1/listings/search \
  -H "Content-Type: application/json" \
  -d '{"city": "Springfield", "pageNumber": 1, "pageSize": 10}'

# Filter by price range
curl -X POST http://localhost:8080/api/v1/listings/search \
  -H "Content-Type: application/json" \
  -d '{"minPrice": 500000, "maxPrice": 600000, "pageNumber": 1, "pageSize": 5}'
```

### Unit Tests
- `backend/src/test/java/` — JUnit 5 tests for RankingService
- Run: `mvn test`

---

## 🎓 Architecture Overview

```
┌─────────────────────────────────────────────────────┐
│  React Frontend (Port 3000 / served from :8080)     │
└──────────────┬──────────────────────────────────────┘
               │ HTTP/REST
┌──────────────▼──────────────────────────────────────┐
│  Spring Boot Backend (Port 8080, Java 21)           │
│  ├─ ListingController → REST endpoints              │
│  ├─ ListingService → Business logic                 │
│  ├─ RankingService → Relevance scoring              │
│  └─ ListingRepository → JPA/Database access         │
└──────────────┬──────────────────────────────────────┘
               │ JDBC
┌──────────────▼──────────────────────────────────────┐
│  PostgreSQL 15 (Port 5432)                          │
│  └─ listings table (10 sample properties)           │
└─────────────────────────────────────────────────────┘
```

---

## 📞 Context for Future Sessions

**Current Working Setup**: Docker + PostgreSQL fully functional and tested  
**Last Modified**: Oct 1, 2026 23:37 UTC  
**Account Used**: humbertoss25@gmail.com  

If picking up work from a different account/device:
1. Clone from git repo (or copy project directory)
2. Run `docker compose up` to verify Docker version works
3. Check STANDALONE_README.md if implementing standalone mode
4. Reference this file for architecture and technical decisions

---

**Project Assessment**: ~90% complete. Docker deployment production-ready. Standalone mode infrastructure exists but needs service-layer wiring (~15 min work).
