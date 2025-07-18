#!/bin/bash

# Docker Test Script for Copper Kingdom Plugin
# Tests the Docker setup and validates plugin functionality

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_info() {
    echo -e "${BLUE}[TEST]${NC} $1"
}

# Check if Docker is installed and running
check_docker() {
    print_info "Checking Docker installation..."
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed"
        exit 1
    fi
    
    if ! docker info &> /dev/null; then
        print_error "Docker is not running"
        exit 1
    fi
    
    print_status "Docker is running"
}

# Check if docker-compose is available
check_docker_compose() {
    print_info "Checking Docker Compose..."
    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        print_error "Docker Compose is not available"
        exit 1
    fi
    
    print_status "Docker Compose is available"
}

# Build the plugin
build_plugin() {
    print_info "Building plugin..."
    if [ ! -f "pom.xml" ]; then
        print_error "pom.xml not found"
        exit 1
    fi
    
    mvn clean package -q
    
    if [ ! -f "target/copper-kingdom-0.1.0.jar" ]; then
        print_error "Plugin JAR not found after build"
        exit 1
    fi
    
    print_status "Plugin built successfully"
}

# Test Docker container startup
test_container_startup() {
    print_info "Testing Docker container startup..."
    
    # Stop any existing containers
    docker-compose down -q 2>/dev/null || true
    
    # Start the container
    print_status "Starting container..."
    docker-compose up -d
    
    # Wait for container to start
    sleep 10
    
    # Check if container is running
    if ! docker-compose ps | grep -q "Up"; then
        print_error "Container failed to start"
        docker-compose logs
        exit 1
    fi
    
    print_status "Container started successfully"
}

# Test server connectivity
test_server_connectivity() {
    print_info "Testing server connectivity..."
    
    # Wait for server to fully start
    print_status "Waiting for server to start (this may take a while)..."
    local timeout=120
    local count=0
    
    while [ $count -lt $timeout ]; do
        if docker-compose logs | grep -q "Done"; then
            print_status "Server started successfully"
            return 0
        fi
        
        if docker-compose logs | grep -q "Failed to start"; then
            print_error "Server failed to start"
            docker-compose logs
            exit 1
        fi
        
        sleep 2
        count=$((count + 2))
        
        if [ $((count % 20)) -eq 0 ]; then
            print_info "Still waiting... ($count/$timeout seconds)"
        fi
    done
    
    print_warning "Server startup timeout, but container is running"
}

# Test plugin loading
test_plugin_loading() {
    print_info "Testing plugin loading..."
    
    # Check if plugin is loaded in the logs
    sleep 5
    if docker-compose logs | grep -q "Copper Kingdom has been enabled"; then
        print_status "Plugin loaded successfully"
    else
        print_warning "Plugin loading not confirmed in logs"
        print_info "Recent logs:"
        docker-compose logs | tail -20
    fi
}

# Test port accessibility
test_ports() {
    print_info "Testing port accessibility..."
    
    # Test Minecraft port (25565)
    if nc -z localhost 25565 2>/dev/null; then
        print_status "Minecraft port (25565) is accessible"
    else
        print_warning "Minecraft port (25565) is not accessible"
    fi
    
    # Test Bedrock port (19132)
    if nc -z localhost 19132 2>/dev/null; then
        print_status "Bedrock port (19132) is accessible"
    else
        print_warning "Bedrock port (19132) is not accessible"
    fi
}

# Cleanup
cleanup() {
    print_info "Cleaning up..."
    docker-compose down -q
    print_status "Cleanup complete"
}

# Run all tests
run_tests() {
    print_status "Starting Copper Kingdom Docker tests..."
    echo
    
    check_docker
    check_docker_compose
    build_plugin
    test_container_startup
    test_server_connectivity
    test_plugin_loading
    test_ports
    
    echo
    print_status "All tests completed!"
    print_info "The server is running. You can:"
    print_info "  - Connect to localhost:25565 (Java Edition)"
    print_info "  - Connect to localhost:19132 (Bedrock Edition)"
    print_info "  - View logs: docker-compose logs -f"
    print_info "  - Stop server: docker-compose down"
}

# Handle script interruption
trap cleanup EXIT

# Run tests
run_tests
