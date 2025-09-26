# JUnit with Mockito Integration Tests

This document describes the comprehensive test suite created for the e-commerce Spring Boot application using JUnit 5 and Mockito.

## Test Structure

The test suite covers all layers of the application:

### 1. Entity Tests

- **UserTest**: Tests the User entity functionality, validation, and security features
- **ProductTest**: Tests the Product entity business logic, stock management, and validations

### 2. Repository Tests (Data Layer)

- **ProductRepositoryTest**: Tests custom queries and CRUD operations for products
- **UserRepositoryTest**: Tests user repository methods and database constraints

### 3. Service Tests (Business Layer)

- **ProductServiceTest**: Unit tests with Mockito for ProductService
- **UserServiceTest**: Unit tests with Mockito for UserService
- **ServiceIntegrationTest**: Integration tests for services with real database

### 4. Controller Tests (Presentation Layer)

- **ProductControllerTest**: Unit tests with Mockito for ProductController
- **AuthControllerTest**: Unit tests with Mockito for AuthController
- **WebIntegrationTest**: Full web integration tests with MockMvc

### 5. Application Tests

- **EcommerceApplicationTest**: Context loading test

## Test Technologies Used

- **JUnit 5**: Main testing framework
- **Mockito**: Mocking framework for unit tests
- **Spring Boot Test**: Integration testing support
- **MockMvc**: Web layer testing
- **TestEntityManager**: JPA testing utilities
- **H2 Database**: In-memory database for tests
- **Spring Security Test**: Security testing support

## Test Coverage

### Entity Layer

- User entity business logic and validation
- Product stock management and price calculations
- Entity lifecycle callbacks (@PrePersist)

### Repository Layer

- Custom JPQL queries
- Basic CRUD operations
- Database constraints and unique validations
- Relationship mappings

### Service Layer

- Business logic validation
- Exception handling
- Transaction management
- Password encoding and security
- Stock management operations

### Controller Layer

- HTTP request/response handling
- Model attribute binding
- View resolution
- Form validation
- Redirect logic
- Security authorization

## Running the Tests

### Run All Tests

```bash
./mvnw test
```

### Run Specific Test Classes

```bash
./mvnw test -Dtest=ProductServiceTest
./mvnw test -Dtest=UserRepositoryTest
```

### Run Tests with Coverage

```bash
./mvnw test jacoco:report
```

## Test Configuration

### Test Properties

- `application-test.properties`: Test-specific configuration
- H2 in-memory database for isolated testing
- Test logging configuration
- Security test configuration

### Test Profiles

Tests run with `@ActiveProfiles("test")` to use test-specific configuration.

## Key Testing Patterns

### 1. Unit Tests with Mockito

```java
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    @Mock
    private Repository repository;

    @InjectMocks
    private Service service;
}
```

### 2. Repository Tests

```java
@DataJpaTest
class RepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private Repository repository;
}
```

### 3. Web Integration Tests

```java
@SpringBootTest
@AutoConfigureWebMvc
@WithMockUser
class ControllerTest {
    @Autowired
    private MockMvc mockMvc;
}
```

### 4. Service Integration Tests

```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ServiceIntegrationTest {
    // Tests with real Spring context
}
```

## Test Data Management

- **@Transactional**: Automatic rollback after each test
- **@DirtiesContext**: Context refresh when needed
- **TestEntityManager**: Direct entity persistence for setup
- **Repository.deleteAll()**: Clean slate for integration tests

## Security Testing

- **@WithMockUser**: Mock authenticated users
- **@WithAnonymousUser**: Test unauthenticated access
- **CSRF Protection**: Test CSRF token validation
- **Role-based Access**: Test different user roles

## Validation Testing

- **Bean Validation**: Test @Valid annotations
- **Custom Validation**: Test business rule validation
- **Error Handling**: Test exception scenarios
- **Constraint Violations**: Test database constraints

## Mock Scenarios Covered

- **Successful Operations**: Happy path testing
- **Exception Handling**: Error scenarios
- **Edge Cases**: Boundary conditions
- **Concurrent Operations**: Multi-user scenarios
- **Data Integrity**: Constraint violations

## Assertions and Verifications

- **State Verification**: Assert expected outcomes
- **Behavior Verification**: Verify mock interactions
- **Exception Testing**: Assert expected exceptions
- **Mock Verification**: Verify method calls and parameters

## Best Practices Implemented

1. **Arrange-Act-Assert**: Clear test structure
2. **One Assertion Per Test**: Focused test cases
3. **Descriptive Test Names**: Clear test intent
4. **Mock Isolation**: Isolated unit tests
5. **Integration Coverage**: End-to-end scenarios
6. **Test Data Builders**: Consistent test data
7. **Clean Tests**: No test interdependencies

This test suite provides comprehensive coverage of the e-commerce application, ensuring reliability, maintainability, and confidence in the codebase.
