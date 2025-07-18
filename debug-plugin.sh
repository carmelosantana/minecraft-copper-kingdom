#!/bin/bash

# Debug Plugin Script for Copper Kingdom
# Provides debugging utilities for plugin development

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
    echo -e "${BLUE}[DEBUG]${NC} $1"
}

# Check plugin status
check_plugin_status() {
    print_info "Checking plugin status..."
    
    if [ -f "target/copper-kingdom-0.1.1 .jar" ]; then
        print_status "Plugin JAR exists"
        print_info "Size: $(ls -lh target/copper-kingdom-0.1.1 .jar | awk '{print $5}')"
        print_info "Modified: $(ls -l target/copper-kingdom-0.1.1 .jar | awk '{print $6, $7, $8}')"
    else
        print_warning "Plugin JAR not found. Run 'make package' first."
    fi
    
    if [ -f "server/plugins/copper-kingdom-0.1.1 .jar" ]; then
        print_status "Plugin installed in server"
    else
        print_warning "Plugin not installed in server"
    fi
}

# Check server logs for plugin messages
check_plugin_logs() {
    print_info "Checking server logs for plugin messages..."
    
    if [ -f "server/logs/latest.log" ]; then
        print_info "Plugin enable/disable messages:"
        grep -i "copper kingdom" server/logs/latest.log | tail -10
        
        print_info "Plugin errors:"
        grep -i "error.*copper" server/logs/latest.log | tail -5
        
        print_info "Plugin warnings:"
        grep -i "warn.*copper" server/logs/latest.log | tail -5
    else
        print_warning "Server log file not found"
    fi
}

# Validate configuration
validate_config() {
    print_info "Validating configuration..."
    
    if [ -f "src/main/resources/config.yml" ]; then
        print_status "Default config exists"
        
        # Check YAML syntax (if yamllint is available)
        if command -v yamllint >/dev/null 2>&1; then
            if yamllint src/main/resources/config.yml >/dev/null 2>&1; then
                print_status "Config YAML syntax is valid"
            else
                print_error "Config YAML syntax errors found"
                yamllint src/main/resources/config.yml
            fi
        fi
    else
        print_error "Default config.yml not found"
    fi
    
    if [ -f "server/plugins/CopperKingdom/config.yml" ]; then
        print_status "Server config exists"
    else
        print_warning "Server config not found (plugin may not have run yet)"
    fi
}

# Test plugin commands
test_commands() {
    print_info "Available plugin commands to test:"
    print_info "  /copperkingdom help"
    print_info "  /copperkingdom give copper_sword"
    print_info "  /copperkingdom give copper_axe"
    print_info "  /copperkingdom give copper_pickaxe"
    print_info "  /copperkingdom reload"
    
    if [ -f "server/logs/latest.log" ]; then
        print_info "Recent command usage:"
        grep "issued server command" server/logs/latest.log | grep -i copper | tail -5
    fi
}

# Check Java compilation
check_compilation() {
    print_info "Checking Java compilation..."
    
    # Compile without packaging
    mvn compile -q
    
    if [ $? -eq 0 ]; then
        print_status "Compilation successful"
    else
        print_error "Compilation failed"
        return 1
    fi
    
    # Check for common issues
    print_info "Checking for common issues..."
    
    # Check imports
    grep -r "import.*Paper" src/main/java/ 2>/dev/null || print_warning "No Paper imports found"
    grep -r "import.*Bukkit" src/main/java/ 2>/dev/null && print_status "Bukkit imports found"
    
    # Check event handlers
    grep -r "@EventHandler" src/main/java/ 2>/dev/null && print_status "Event handlers found"
    
    # Check plugin.yml
    if [ -f "src/main/resources/plugin.yml" ]; then
        print_status "plugin.yml exists"
        grep -q "main:" src/main/resources/plugin.yml && print_status "Main class specified"
        grep -q "api-version:" src/main/resources/plugin.yml && print_status "API version specified"
    else
        print_error "plugin.yml not found"
    fi
}

# Performance test
performance_test() {
    print_info "Running performance tests..."
    
    # Build with Maven profiling
    print_info "Building with profiling..."
    mvn clean package -q -Duser.timezone=UTC
    
    if [ $? -eq 0 ]; then
        print_status "Build completed successfully"
        
        # Check JAR size
        if [ -f "target/copper-kingdom-0.1.1 .jar" ]; then
            local size=$(stat -f%z "target/copper-kingdom-0.1.1 .jar" 2>/dev/null || stat -c%s "target/copper-kingdom-0.1.1 .jar" 2>/dev/null)
            print_info "JAR size: ${size} bytes"
            
            if [ "$size" -gt 1048576 ]; then  # 1MB
                print_warning "JAR is quite large (>1MB)"
            else
                print_status "JAR size is reasonable"
            fi
        fi
    else
        print_error "Build failed"
    fi
}

# Clean debug data
clean_debug() {
    print_info "Cleaning debug data..."
    
    # Clean Maven artifacts
    mvn clean -q
    
    # Clean server data (be careful!)
    if [ -d "server/world" ]; then
        print_warning "Found world data - not removing (manual cleanup required)"
    fi
    
    # Clean logs
    if [ -f "server/logs/latest.log" ]; then
        print_info "Backing up and clearing logs..."
        cp server/logs/latest.log "server/logs/latest.log.backup.$(date +%Y%m%d_%H%M%S)"
        > server/logs/latest.log
    fi
    
    print_status "Debug cleanup complete"
}

# Main script logic
case "$1" in
    status)
        check_plugin_status
        ;;
    logs)
        check_plugin_logs
        ;;
    config)
        validate_config
        ;;
    commands)
        test_commands
        ;;
    compile)
        check_compilation
        ;;
    performance)
        performance_test
        ;;
    clean)
        clean_debug
        ;;
    all)
        print_status "Running all debug checks..."
        echo
        check_plugin_status
        echo
        check_compilation
        echo
        validate_config
        echo
        check_plugin_logs
        echo
        test_commands
        ;;
    *)
        echo "Usage: $0 {status|logs|config|commands|compile|performance|clean|all}"
        echo ""
        echo "Debug Commands:"
        echo "  status      - Check plugin JAR and installation status"
        echo "  logs        - Check server logs for plugin messages"
        echo "  config      - Validate configuration files"
        echo "  commands    - Show available plugin commands"
        echo "  compile     - Test Java compilation"
        echo "  performance - Run performance tests"
        echo "  clean       - Clean debug data"
        echo "  all         - Run all debug checks"
        exit 1
        ;;
esac

exit 0
