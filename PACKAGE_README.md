# Real Estate Professionals - Distribution Package

This package contains a complete Real Estate listing service application with two deployment options:

## Option 1: Docker with PostgreSQL (Recommended for Production)

Best for complete, scalable deployments.

### Requirements:
- Docker & Docker Compose

### Quick Start:
```bash
docker compose up
```

Then visit: `http://localhost:8080`

### Features:
- Relational database (PostgreSQL)
- Persistent data storage
- Full search capabilities
- Pre-loaded with 10 sample properties
- Suitable for production environments

---

## Option 2: Standalone with JSON Storage (Coming Soon)

Simple deployment with minimal dependencies.

### Requirements:
- Java 21+

### Quick Start:
```bash
# Linux/Mac
./run.sh

# Windows
run.bat
```

Then visit: `http://localhost:8080`

### Features:
- File-based JSON storage (no database needed)
- Single JAR deployment
- Perfect for demonstrations
- Minimal resource usage

**Status**: File storage option is being finalized for the next release. Currently, use Docker option for full functionality.

---

## Project Structure

```
.
├── backend/                  # Spring Boot backend (Java 21)
│   ├── src/main/java/       # Source code
│   ├── src/test/            # Unit tests  
│   ├── pom.xml             # Maven configuration
│   └── Dockerfile          # Backend container
├── frontend/                # React frontend
│   ├── src/                # React components
│   ├── public/             # Static assets
│   ├── package.json        # Dependencies
│   └── Dockerfile          # Frontend container
├── terraform/              # AWS infrastructure (optional)
├── docker-compose.yml      # Service orchestration
├── run.sh                  # Standalone launcher (Linux/Mac)
├── run.bat                 # Standalone launcher (Windows)
├── README.md               # Full documentation
├── SETUP_GUIDE.md          # Setup instructions
└── STANDALONE_README.md    # Standalone mode guide
```

---

## API Endpoints

### Search Listings
```
POST /api/v1/listings/search
Content-Type: application/json

{
  "city": "Springfield",
  "minPrice": 300000,
  "maxPrice": 500000,
  "minBedrooms": 2,
  "keyword": "pet friendly",
  "targetBudget": 450000,
  "pageNumber": 1,
  "pageSize": 10
}
```

---

## Quick Facts

- **Frontend**: React 18 (pre-built, served from backend)
- **Backend**: Spring Boot 3.3.5 with Java 21
- **Database**: PostgreSQL 15 (with Docker)
- **Storage**: JSON files (standalone version - in development)
- **Search**: Full-text keyword search with relevance ranking
- **Sample Data**: 10 real estate listings pre-loaded

---

## Support & Troubleshooting

### Docker Mode Issues
- Ensure Docker and Docker Compose are installed
- Check port availability (8080, 3000, 5432)
- Run `docker compose logs` for detailed error messages

### Standalone Mode Issues (Coming Soon)
- Ensure Java 21+ is installed
- Check if port 8080 is available
- Review `data/listings.json` for data issues

---

## Next Steps

1. Choose your deployment option (Docker recommended)
2. Follow the "Quick Start" section above
3. Open `http://localhost:8080` in your browser
4. Try searching for properties using the filter options

---

**For detailed information, see README.md and SETUP_GUIDE.md**
