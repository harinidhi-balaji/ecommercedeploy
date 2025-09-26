# Docker Setup for E-commerce Application

This document provides instructions for running the E-commerce application using Docker.

## Prerequisites

- Docker Desktop installed and running
- Docker Compose (included with Docker Desktop)
- At least 4GB of free disk space
- Ports 8080 and 5432 available

## Quick Start

### Option 1: Using Docker Compose (Recommended)

1. **Build and start the application:**

   ```bash
   ./docker-run.sh build
   ./docker-run.sh start
   ```

2. **Access the application:**
   - Application: http://localhost:8080
   - Database: localhost:5432

### Option 2: Manual Docker Compose

1. **Build the Docker image:**

   ```bash
   docker-compose build
   ```

2. **Start the services:**

   ```bash
   docker-compose up -d
   ```

3. **Stop the services:**
   ```bash
   docker-compose down
   ```

## Docker Management Script

The `docker-run.sh` script provides easy management of the Docker environment:

```bash
# Build the application
./docker-run.sh build

# Start services
./docker-run.sh start

# Stop services
./docker-run.sh stop

# Restart services
./docker-run.sh restart

# View logs (all services)
./docker-run.sh logs

# View logs for specific service
./docker-run.sh logs app
./docker-run.sh logs postgres

# Check service status
./docker-run.sh status

# Clean up everything
./docker-run.sh clean

# Show help
./docker-run.sh help
```

## Services

### Application Service (`app`)

- **Image:** Custom build from Dockerfile
- **Port:** 8080
- **Environment:** Docker profile with PostgreSQL
- **Health Check:** HTTP endpoint check
- **Volumes:** Logs and uploads

### Database Service (`postgres`)

- **Image:** postgres:15-alpine
- **Port:** 5432
- **Database:** mydb
- **Username:** postgres
- **Password:** password123
- **Health Check:** PostgreSQL ready check
- **Initialization:** Runs SQL scripts on first start

## Configuration

### Environment Variables

The Docker setup uses the following environment variables:

| Variable                   | Value                                | Description              |
| -------------------------- | ------------------------------------ | ------------------------ |
| SPRING_PROFILES_ACTIVE     | docker                               | Activates Docker profile |
| SPRING_DATASOURCE_URL      | jdbc:postgresql://postgres:5432/mydb | Database connection      |
| SPRING_DATASOURCE_USERNAME | postgres                             | Database username        |
| SPRING_DATASOURCE_PASSWORD | password123                          | Database password        |

### Profiles

- **docker:** Used in containerized environment
- Configuration file: `application-docker.properties`

## Volumes

| Volume        | Purpose                     |
| ------------- | --------------------------- |
| postgres_data | PostgreSQL data persistence |
| app_logs      | Application log files       |
| app_uploads   | Uploaded files storage      |

## Networking

All services run on the `ecommerce-network` bridge network, allowing inter-service communication.

## Database Initialization

On first startup, the PostgreSQL container will:

1. Create the `mydb` database
2. Execute `database_setup.sql` for schema creation
3. Execute `add_products.sql` for initial data

## Health Checks

Both services include health checks:

- **PostgreSQL:** Checks if database is ready to accept connections
- **Application:** Checks HTTP health endpoint

## Troubleshooting

### Common Issues

1. **Port conflicts:**

   ```bash
   # Check what's using the ports
   lsof -i :8080
   lsof -i :5432
   ```

2. **Docker not running:**

   ```bash
   # Check Docker status
   docker info
   ```

3. **Build failures:**

   ```bash
   # Clean build
   docker-compose build --no-cache
   ```

4. **Database connection issues:**

   ```bash
   # Check PostgreSQL logs
   ./docker-run.sh logs postgres
   ```

5. **Application startup issues:**
   ```bash
   # Check application logs
   ./docker-run.sh logs app
   ```

### Useful Commands

```bash
# Enter PostgreSQL container
docker-compose exec postgres psql -U postgres -d mydb

# Enter application container
docker-compose exec app bash

# View container resource usage
docker stats

# Remove everything and start fresh
./docker-run.sh clean
./docker-run.sh build
./docker-run.sh start
```

## Development Mode

For development with auto-reload:

1. **Modify docker-compose.yml:**

   ```yaml
   app:
     build: .
     volumes:
       - ./src:/app/src # Mount source code
     environment:
       - SPRING_DEVTOOLS_RESTART_ENABLED=true
   ```

2. **Add Spring DevTools to pom.xml:**
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-devtools</artifactId>
       <scope>runtime</scope>
   </dependency>
   ```

## Production Considerations

For production deployment:

1. **Security:**

   - Change default passwords
   - Use secrets management
   - Enable SSL/TLS
   - Run containers as non-root

2. **Performance:**

   - Adjust JVM heap sizes
   - Configure connection pooling
   - Use production database

3. **Monitoring:**

   - Enable actuator endpoints
   - Set up log aggregation
   - Configure health checks

4. **Backup:**
   - Regular database backups
   - Volume backup strategy

## Support

For issues related to Docker setup, check:

1. Docker logs: `./docker-run.sh logs`
2. Service status: `./docker-run.sh status`
3. Container health: `docker-compose ps`
