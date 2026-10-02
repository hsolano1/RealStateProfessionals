# Real Estate Professionals - Standalone Deployment

This is a standalone distribution of the Real Estate Professionals listing service that **requires only Java 21** to run.

## Quick Start

### Prerequisites
- **Java 21 or higher** - [Download Java](https://www.oracle.com/java/technologies/downloads/#java21)
- **Maven 3.8+** (optional - will download automatically if not present)

### Running the Application

#### On Linux/macOS:
```bash
chmod +x run.sh
./run.sh
```

#### On Windows:
```bash
run.bat
```

The application will:
1. Build the backend automatically
2. Start the server on `http://localhost:8080`
3. Be accessible from your browser immediately

### File Storage
Data is stored in `data/listings.json` in the application directory. This file is created automatically on first run and persisted across sessions.

## Features

✅ **No Docker required** - Just Java  
✅ **No database installation** - Uses JSON file storage  
✅ **Pre-built frontend** - Served directly from backend  
✅ **Full search functionality** - Filter by city, price, bedrooms, keywords  
✅ **10 sample properties** - Pre-loaded on first run  

## Usage

1. **Start the application** using run.sh or run.bat
2. **Open your browser** to `http://localhost:8080`
3. **Search properties** using the search form:
   - Filter by city (Springfield, Fairfax, Reston, Vienna, Manassas, Chantilly)
   - Filter by price range
   - Filter by number of bedrooms
   - Search by keywords in property description or address

## Data

Sample data includes 10 real estate listings with:
- Full property details (address, city, price, bedrooms, bathrooms, sqft)
- Property descriptions and status
- Geographic coordinates (latitude/longitude)
- Listing dates for relevance ranking

All data is automatically saved to `data/listings.json`.

## Troubleshooting

### "Java not found"
- Ensure Java 21+ is installed: `java -version`
- Add Java to your PATH environment variable

### Port 8080 already in use
- Change the port by editing the `--server.port` in run.sh or run.bat
- Or stop the application using that port

### "Build failed" error
- Ensure you have internet connection (first run downloads dependencies)
- Try deleting the `backend/target` folder and running again
- Ensure Maven can access central repositories

## Advanced Usage

### Using with PostgreSQL (Docker)
If you prefer to use PostgreSQL with Docker:
```bash
docker compose up
```

This starts PostgreSQL, backend, and frontend in Docker containers.

### Custom configuration
Edit `backend/src/main/resources/application-file.properties` to customize:
- Server port
- Logging levels
- Data directory location

## Architecture

- **Backend**: Spring Boot 3.3.5 with Java 21
- **Frontend**: React 18 (pre-built, served statically)
- **Storage**: JSON files (can be upgraded to PostgreSQL)
- **API**: REST API at `/api/v1/listings/search`

## API Endpoint

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

## Performance

- **Startup time**: ~2-3 seconds
- **Search latency**: <100ms for typical queries
- **Memory usage**: ~200MB for entire application

## Future Upgrades

To upgrade from JSON to PostgreSQL:
1. Install and run PostgreSQL
2. Use `docker compose up` instead
3. Application will automatically migrate to database storage

---

**Enjoy using Real Estate Professionals!**
