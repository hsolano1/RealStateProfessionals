@echo off

REM Real Estate Professionals - Startup Script (Windows)
REM Requires: Java 21+

echo =========================================
echo Real Estate Professionals - Real Estate Listing Service
echo =========================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 21 or higher
    pause
    exit /b 1
)

echo Java is available
echo.

REM Build backend
echo Building backend...
cd backend
call mvn clean package -DskipTests -q
if errorlevel 1 (
    echo ERROR: Backend build failed
    cd ..
    pause
    exit /b 1
)
cd ..

echo Build complete!
echo.
echo Starting Real Estate Professionals...
echo Using file-based storage (JSON)
echo.

REM Run the application
java -jar backend\target\realestate-service-1.0.0.jar --spring.profiles.active=file

echo.
echo Application stopped.
pause
