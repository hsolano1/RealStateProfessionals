#!/bin/bash

# Real Estate Professionals - Startup Script
# Requires: Java 21+

echo "========================================="
echo "Real Estate Professionals - Real Estate Listing Service"
echo "========================================="
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH"
    echo "Please install Java 21 or higher"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "\K[^"]*')
echo "Java version: $JAVA_VERSION"
echo ""

# Build backend
echo "Building backend..."
cd backend
mvn clean package -DskipTests -q
if [ $? -ne 0 ]; then
    echo "ERROR: Backend build failed"
    exit 1
fi
cd ..

echo "Build complete!"
echo ""
echo "Starting Real Estate Professionals..."
echo "Using file-based storage (JSON)"
echo ""

# Run the application
java -jar backend/target/realestate-service-1.0.0.jar --spring.profiles.active=file

echo ""
echo "Application stopped."
