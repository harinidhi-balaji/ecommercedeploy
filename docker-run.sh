#!/bin/bash

# E-commerce Docker Management Script

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if Docker is running
check_docker() {
    if ! docker info >/dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
}

# Function to build the application
build() {
    print_status "Building Docker image for E-commerce application..."
    docker-compose build --no-cache
    print_success "Docker image built successfully!"
}

# Function to start services
start() {
    print_status "Starting E-commerce application with PostgreSQL..."
    docker-compose up -d
    
    print_status "Waiting for services to be healthy..."
    sleep 30
    
    print_status "Checking service status..."
    docker-compose ps
    
    print_success "Application started successfully!"
    print_status "Application is available at: http://localhost:8080"
    print_status "Database is available at: localhost:5432"
}

# Function to stop services
stop() {
    print_status "Stopping E-commerce application..."
    docker-compose down
    print_success "Application stopped successfully!"
}

# Function to restart services
restart() {
    print_status "Restarting E-commerce application..."
    docker-compose restart
    print_success "Application restarted successfully!"
}

# Function to view logs
logs() {
    if [ -n "$2" ]; then
        print_status "Showing logs for service: $2"
        docker-compose logs -f "$2"
    else
        print_status "Showing logs for all services..."
        docker-compose logs -f
    fi
}

# Function to clean up
clean() {
    print_warning "This will remove all containers, images, and volumes. Are you sure? (y/N)"
    read -r response
    if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
        print_status "Cleaning up Docker resources..."
        docker-compose down -v --rmi all --remove-orphans
        print_success "Cleanup completed!"
    else
        print_status "Cleanup cancelled."
    fi
}

# Function to show status
status() {
    print_status "Service Status:"
    docker-compose ps
    
    print_status "Container Resource Usage:"
    docker stats --no-stream $(docker-compose ps -q) 2>/dev/null || echo "No running containers"
}

# Function to show help
show_help() {
    echo "E-commerce Docker Management Script"
    echo ""
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  build     Build the Docker image"
    echo "  start     Start the application and database"
    echo "  stop      Stop the application and database"
    echo "  restart   Restart the application"
    echo "  logs      Show application logs (optional: specify service name)"
    echo "  status    Show service status and resource usage"
    echo "  clean     Remove all containers, images, and volumes"
    echo "  help      Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 build"
    echo "  $0 start"
    echo "  $0 logs app"
    echo "  $0 logs postgres"
}

# Main script logic
check_docker

case "${1:-help}" in
    build)
        build
        ;;
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        restart
        ;;
    logs)
        logs "$@"
        ;;
    status)
        status
        ;;
    clean)
        clean
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        print_error "Unknown command: $1"
        echo ""
        show_help
        exit 1
        ;;
esac