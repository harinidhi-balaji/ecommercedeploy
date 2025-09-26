# Troubleshooting Guide

## Common Issues and Solutions

### Issue: Whitelabel Error Page on "Buy Now" Button

**Problem**: When clicking the "Buy Now" button in the shopping cart, you get a whitelabel error page instead of the order confirmation.

**Root Causes & Solutions**:

1. **Missing Order Detail Template** ✅ **FIXED**
   - **Solution**: The `user/order-detail.html` template has been created
   - **Location**: `src/main/resources/templates/user/order-detail.html`

2. **Database Connection Issues**
   - **Check**: Ensure MySQL is running and database exists
   - **Solution**:
     ```bash
     # Start MySQL service
     sudo systemctl start mysql

     # Create database
     mysql -u root -p < database_setup.sql
     ```

3. **Configuration Issues**
   - **Check**: Verify `application.properties` has correct database settings
   - **Solution**: Update the database credentials:
     ```properties
     DB_URL=jdbc:mysql://localhost:3306/ecommerce_db
     DB_USERNAME=root
     DB_PASSWORD=your_actual_password
     ```

4. **Missing Dependencies**
   - **Check**: Maven dependencies are correctly resolved
   - **Solution**: Run `./mvnw clean install` to download dependencies

5. **Security Configuration Issues** ✅ **FIXED**
   - **Solution**: Added `@Lazy` annotation to prevent circular dependency
   - **Location**: `src/main/java/com/ecommerce/config/SecurityConfig.java:24`

### Step-by-Step Debugging

1. **Check Application Logs**:
   ```bash
   # Look for errors in console output when starting the app
   tail -f logs/ecommerce.log
   ```

2. **Verify Database Connection**:
   ```bash
   # Test MySQL connection
   mysql -u root -p -e "USE ecommerce_db; SHOW TABLES;"
   ```

3. **Check if Order Creation Works**:
   - Add items to cart
   - Check if cart displays correctly
   - Try creating order and check server logs

4. **Verify Templates Exist**:
   ```bash
   # Check if all required templates exist
   ls -la src/main/resources/templates/user/
   ls -la src/main/resources/templates/admin/
   ```

### Quick Fixes Applied

✅ **Created Missing Templates**:
- `user/order-detail.html` - Order confirmation page
- `admin/edit-product.html` - Product editing form
- `admin/manage-orders.html` - Order management page
- `admin/view-order.html` - Individual order view
- `admin/manage-users.html` - User management page

✅ **Fixed Dependencies**:
- Updated MySQL connector to `mysql-connector-j`
- Added proper scope for runtime dependency

✅ **Enhanced Order Flow**:
- Added success parameter to order detail redirect
- Improved error handling in order creation

### Testing the Fix

1. **Start the Application**:
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Test User Flow**:
   - Register/Login as user
   - Add products to cart
   - Click "Buy Now" button
   - Should redirect to order detail page with success message

3. **Test Admin Flow**:
   - Login as admin (admin/admin123)
   - Navigate to admin dashboard
   - Manage products and orders

### Error Messages and Solutions

| Error | Cause | Solution |
|-------|-------|----------|
| "Whitelabel Error Page" | Missing template | Check template exists in correct location |
| "Template might not exist" | Template path wrong | Verify controller return values match template names |
| "Cannot resolve template" | Thymeleaf configuration | Check `application.yml` thymeleaf settings |
| "Access Denied" | Security misconfiguration | Verify user roles and security mappings |
| "Database connection failed" | MySQL not running/wrong config | Check MySQL service and connection settings |

### Performance Optimization

If the application is slow:

1. **Check Database Queries**:
   - Enable SQL logging: `SHOW_SQL=true`
   - Look for N+1 query problems

2. **Optimize Database**:
   - Ensure indexes are created (check `schema.sql`)
   - Monitor connection pool usage

3. **Memory Settings**:
   ```bash
   # For production, increase memory
   java -Xms1g -Xmx2g -jar target/ecommerce-app-1.0.0.jar
   ```

### Getting Help

If issues persist:

1. **Check Application Logs**: Look for stack traces and error messages
2. **Verify Configuration**: Double-check all settings in `application.properties`
3. **Test Database**: Ensure database is accessible and populated
4. **Clean Build**: Run `./mvnw clean package` to rebuild from scratch

### Common Configuration Fixes

**Database Connection Pool**:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 20000
```

**Security Debugging**:
```yaml
logging:
  level:
    org.springframework.security: DEBUG
```

**Template Resolution**:
```yaml
spring:
  thymeleaf:
    prefix: classpath:/templates/
    suffix: .html
    cache: false  # For development
```

This guide should help resolve the "Buy Now" button issue and other common problems.