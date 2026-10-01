#!/bin/bash
set -e

echo "Starting EC2 instance setup..."

# Update system
sudo yum update -y
sudo yum install -y git curl wget

# Install Docker
sudo yum install -y docker
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker ec2-user

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

echo "Docker and Docker Compose installed successfully"

# Clone repository (if available)
# cd /home/ec2-user
# git clone <your-repo-url>
# cd RealStateProfessionals

# Create application directory
mkdir -p /opt/realestate
cd /opt/realestate

# Create environment file
cat > .env <<EOF
SPRING_DATASOURCE_URL=jdbc:postgresql://${db_host}:${db_port}/${db_name}
SPRING_DATASOURCE_USERNAME=${db_username}
SPRING_DATASOURCE_PASSWORD=${db_password}
SPRING_JPA_HIBERNATE_DDL_AUTO=update
REACT_APP_API_URL=http://$(hostname -I | awk '{print $1}'):8080
EOF

# Create docker-compose.yml for production
cat > docker-compose.yml <<'COMPOSE'
version: '3.8'

services:
  backend:
    image: realestate-backend:latest
    container_name: realestate-backend
    environment:
      SPRING_DATASOURCE_URL: ${SPRING_DATASOURCE_URL}
      SPRING_DATASOURCE_USERNAME: ${SPRING_DATASOURCE_USERNAME}
      SPRING_DATASOURCE_PASSWORD: ${SPRING_DATASOURCE_PASSWORD}
      SPRING_JPA_HIBERNATE_DDL_AUTO: ${SPRING_JPA_HIBERNATE_DDL_AUTO}
    ports:
      - "8080:8080"
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/v1/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s

  frontend:
    image: realestate-frontend:latest
    container_name: realestate-frontend
    environment:
      REACT_APP_API_URL: http://localhost:8080
    ports:
      - "3000:3000"
    depends_on:
      - backend
    restart: unless-stopped
COMPOSE

echo "Docker Compose configuration created"
echo "Setup completed successfully!"
echo "Next steps:"
echo "1. Clone the repository to /opt/realestate"
echo "2. Build Docker images: docker-compose build"
echo "3. Start services: docker-compose up -d"
echo "4. Access the application at http://$(hostname -I | awk '{print $1}'):3000"
