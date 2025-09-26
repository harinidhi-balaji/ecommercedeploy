#!/bin/bash

echo "=========================================="
echo "ECommerce Application Build Script"
echo "=========================================="

# Check for Java
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Display Java version
echo "Java version:"
java -version
echo ""

# Clean and compile
echo "Cleaning and compiling the application..."
mvn clean compile && cd ..

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
    echo ""

    # Package the application
    echo "Packaging the application..."
    cd ecomm && mvn package -DskipTests && cd ..

    if [ $? -eq 0 ]; then
        echo "✅ Build successful!"
        echo ""
        echo "📦 JAR file location: target/ecommerce-app-1.0.0.jar"
        echo ""
        echo "To run the application:"
        echo "1. Set up your MySQL database using database_setup.sql"
        echo "2. Configure application.properties with your database settings"
        echo "3. Run: java -jar target/ecommerce-app-1.0.0.jar --spring.config.location=file:./application.properties"
        echo ""
        echo "Default credentials:"
        echo "  Admin - Username: admin, Password: admin123"
        echo "  User  - Username: testuser, Password: user123"
    else
        echo "❌ Build failed during packaging"
        exit 1
    fi
else
    echo "❌ Build failed during compilation"
    exit 1
fi