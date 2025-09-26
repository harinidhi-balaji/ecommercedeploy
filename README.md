# ECommerce Application

A complete, production-ready ecommerce application built with Spring Boot 3.x, Thymeleaf, MySQL, and Bootstrap 5.

## 🚀 Features

### User Features
- **User Registration & Authentication** - Secure user registration and login with Spring Security
- **Product Catalog** - Browse products with search and filter functionality
- **Shopping Cart** - Add/remove items, update quantities, persistent cart across sessions
- **Order Management** - Place orders, view order history, cancel orders
- **Responsive Design** - Mobile-friendly interface with Bootstrap 5

### Admin Features
- **Admin Dashboard** - Overview of orders, products, revenue, and low stock alerts
- **Product Management** - Add, edit, delete products with image upload support
- **Order Management** - View all orders, update order status, order details
- **User Management** - View registered users and their order history
- **Inventory Tracking** - Stock management and low stock alerts

### Technical Features
- **Spring Security** - Role-based access control (USER, ADMIN)
- **MySQL Database** - Robust data persistence with proper relationships
- **External Configuration** - Environment-specific configuration support
- **Responsive UI** - Bootstrap 5 with custom CSS styling
- **RESTful Architecture** - Clean MVC pattern implementation
- **Session Management** - Secure session handling with remember-me functionality

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.0
- **Frontend**: Thymeleaf, Bootstrap 5, JavaScript
- **Database**: MySQL 8.0
- **Security**: Spring Security 6
- **Build Tool**: Maven
- **Styling**: Bootstrap 5, Custom CSS

## 📋 Prerequisites

Before running the application, ensure you have:

- **Java 17** or higher installed
- **MySQL 8.0** or higher installed and running
- **Maven 3.6+** (optional, wrapper included)
- **Git** (optional, for cloning)

## 🔧 Installation & Setup

### 1. Database Setup

#### Option A: Using the complete setup script
```bash
# Connect to MySQL as root user
mysql -u root -p

# Run the complete setup script
source database_setup.sql
```

#### Option B: Manual setup
```bash
# Connect to MySQL
mysql -u root -p

# Create database
CREATE DATABASE ecommerce_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Create user (optional but recommended)
CREATE USER 'ecommerce_user'@'localhost' IDENTIFIED BY 'ecommerce_password';
GRANT ALL PRIVILEGES ON ecommerce_db.* TO 'ecommerce_user'@'localhost';
FLUSH PRIVILEGES;

# Use the database and run schema
USE ecommerce_db;
source src/main/resources/schema.sql
```

### 2. Application Configuration

The application uses external configuration for deployment flexibility.

#### For Development:
```bash
# Copy the sample configuration
cp application.properties application-dev.properties

# Edit the database settings
DB_URL=jdbc:mysql://localhost:3306/ecommerce_db
DB_USERNAME=root
DB_PASSWORD=your_password
SPRING_PROFILES_ACTIVE=dev
```

#### For Production:
```bash
# Copy the sample configuration
cp application.properties application-prod.properties

# Configure production settings
DB_URL=jdbc:mysql://your-server:3306/ecommerce_db
DB_USERNAME=ecommerce_user
DB_PASSWORD=secure_password
SPRING_PROFILES_ACTIVE=prod
COOKIE_SECURE=true
DDL_AUTO=validate
```

### 3. Build the Application

```bash
# Clean and build the application
./mvnw clean package

# Skip tests if needed
./mvnw clean package -DskipTests
```

### 4. Run the Application

#### Development Mode:
```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Or using the JAR with external config
java -jar target/ecommerce-app-1.0.0.jar --spring.config.location=file:./application-dev.properties
```

#### Production Mode:
```bash
# Run with external configuration
java -jar target/ecommerce-app-1.0.0.jar --spring.config.location=file:./application-prod.properties

# With JVM optimization for production
java -Xms1g -Xmx2g -XX:+UseG1GC -jar target/ecommerce-app-1.0.0.jar --spring.config.location=file:./application.properties
```

## 🔑 Default Login Credentials

The application comes with pre-configured users:

### Admin Account
- **Username**: `admin`
- **Password**: `admin123`
- **Role**: ADMIN
- **Email**: admin@ecommerce.com

### Test User Account
- **Username**: `testuser`
- **Password**: `user123`
- **Role**: USER
- **Email**: user@test.com

## 🌐 Application URLs

Once the application is running:

- **Application**: http://localhost:8080
- **Admin Login**: http://localhost:8080/login (use admin credentials)
- **User Registration**: http://localhost:8080/register
- **Admin Dashboard**: http://localhost:8080/admin/dashboard (admin only)

## 📁 Project Structure

```
ecommerce/
├── src/
│   ├── main/
│   │   ├── java/com/ecommerce/
│   │   │   ├── config/           # Security and Web configuration
│   │   │   ├── controller/       # MVC Controllers
│   │   │   ├── entity/          # JPA Entities
│   │   │   ├── repository/      # Data repositories
│   │   │   ├── service/         # Business logic
│   │   │   └── EcommerceApplication.java
│   │   └── resources/
│   │       ├── templates/       # Thymeleaf templates
│   │       ├── static/          # CSS, JS, images
│   │       ├── application.yml  # Internal configuration
│   │       └── schema.sql       # Database schema
│   └── test/                    # Test files
├── application.properties       # External configuration template
├── database_setup.sql          # Complete database setup
├── pom.xml                     # Maven configuration
└── README.md                   # This file
```

## 🐳 Docker Deployment (Optional)

### Create Dockerfile:
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/ecommerce-app-1.0.0.jar app.jar
COPY application.properties application.properties

EXPOSE 8080

CMD ["java", "-jar", "app.jar", "--spring.config.location=file:./application.properties"]
```

### Docker Compose:
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: ecommerce_db
      MYSQL_USER: ecommerce_user
      MYSQL_PASSWORD: ecommerce_password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    environment:
      DB_URL: jdbc:mysql://mysql:3306/ecommerce_db
      DB_USERNAME: ecommerce_user
      DB_PASSWORD: ecommerce_password

volumes:
  mysql_data:
```

## 🔧 Configuration Reference

### Database Configuration
```properties
# MySQL Connection
DB_URL=jdbc:mysql://localhost:3306/ecommerce_db
DB_USERNAME=your_username
DB_PASSWORD=your_password

# Connection Pool Settings
spring.datasource.hikari.maximum-pool-size=50
spring.datasource.hikari.minimum-idle=10
```

### Security Configuration
```properties
# Remember Me Key (change for production)
REMEMBER_ME_KEY=your-secure-random-key

# Cookie Security
COOKIE_SECURE=true

# Session Timeout
server.servlet.session.timeout=30m
```

### Performance Tuning
```properties
# JPA Settings
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Caching
CACHE_ENABLED=true
THYMELEAF_CACHE=true

# Logging
LOG_LEVEL=INFO
```

## 🚀 Deployment Guide

### 1. Prepare for Production

1. **Update Configuration**: Use production values in `application.properties`
2. **Database Setup**: Ensure MySQL is properly configured with backups
3. **Security**: Use HTTPS, secure passwords, and proper firewall rules
4. **Monitoring**: Configure logging and monitoring tools

### 2. Build Production JAR

```bash
# Set production profile
export SPRING_PROFILES_ACTIVE=prod

# Build optimized JAR
./mvnw clean package -Pprod

# Verify the JAR
java -jar target/ecommerce-app-1.0.0.jar --version
```

### 3. Server Deployment

```bash
# Create application directory
mkdir -p /opt/ecommerce
cd /opt/ecommerce

# Copy files
cp /path/to/ecommerce-app-1.0.0.jar ./
cp /path/to/application.properties ./

# Create service user
sudo useradd -r -s /bin/false ecommerce

# Set permissions
sudo chown -R ecommerce:ecommerce /opt/ecommerce

# Create systemd service
sudo nano /etc/systemd/system/ecommerce.service
```

### 4. Systemd Service Configuration

```ini
[Unit]
Description=ECommerce Application
After=mysql.service

[Service]
Type=simple
User=ecommerce
ExecStart=/usr/bin/java -jar /opt/ecommerce/ecommerce-app-1.0.0.jar --spring.config.location=file:/opt/ecommerce/application.properties
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
# Enable and start service
sudo systemctl enable ecommerce
sudo systemctl start ecommerce
sudo systemctl status ecommerce
```

## 🔍 Troubleshooting

### Common Issues

1. **Database Connection Issues**
   ```bash
   # Check MySQL status
   sudo systemctl status mysql

   # Test connection
   mysql -u ecommerce_user -p ecommerce_db
   ```

2. **Port Already in Use**
   ```bash
   # Change port in configuration
   SERVER_PORT=8081

   # Or kill process using port 8080
   sudo lsof -t -i:8080 | xargs kill -9
   ```

3. **Permission Denied**
   ```bash
   # Check file permissions
   ls -la application.properties

   # Fix permissions
   chmod 644 application.properties
   ```

4. **Out of Memory**
   ```bash
   # Increase JVM memory
   java -Xms1g -Xmx2g -jar ecommerce-app-1.0.0.jar
   ```

### Logs and Debugging

```bash
# Application logs
tail -f logs/ecommerce.log

# System logs
sudo journalctl -u ecommerce -f

# Enable debug logging
LOG_LEVEL=DEBUG
```

## 📊 Database Schema

The application uses the following main tables:

- **users** - User accounts and authentication
- **products** - Product catalog with inventory
- **cart** - Shopping cart items
- **orders** - Order records
- **order_items** - Individual items in orders

Key relationships:
- Users can have multiple orders and cart items
- Orders contain multiple order items
- Products are referenced by cart and order items

## 🔐 Security Features

- **Password Encryption** - BCrypt hashing for user passwords
- **CSRF Protection** - Built-in CSRF token validation
- **Session Management** - Secure session handling with timeout
- **Role-based Access** - Separate admin and user access levels
- **SQL Injection Prevention** - Parameterized queries with JPA

## 📈 Performance Considerations

- **Connection Pooling** - HikariCP for efficient database connections
- **Lazy Loading** - JPA lazy loading for performance optimization
- **Caching** - Application-level caching for frequently accessed data
- **Optimized Queries** - Custom repository methods with proper indexing

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions:
- Create an issue in the GitHub repository
- Check the troubleshooting section above
- Review the configuration documentation

## 🎯 Future Enhancements

Potential improvements for future versions:
- Payment gateway integration
- Email notifications for orders
- Product reviews and ratings
- Advanced search and filtering
- Order tracking system
- Inventory management dashboard
- REST API for mobile apps
- Multi-language support

---

**Happy coding! 🎉**