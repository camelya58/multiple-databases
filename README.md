# multiple-databases

Simple project with Spring Data Jpa and multiple databases.

Stack: Spring Boot, Spring Data Jpa,  Spring Data Jdbc, Postgres, Lombok, H2, Swagger.

## Step 1 
Create project with [spring initializr](https://start.spring.io/).
Add all the dependencies listed on the stack.

## Step 2
Firstly, create two simple entities in different packages that are in a separate database.

package - model.user
```java
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private int age;
}
```
package - model.product
```java
@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    private int id;

    private String description;

    private double price;
}
```

## Step 3
Secondly, create two different repository in different packages.

package - repository.user
```java
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findAllByAgeGreaterThanEqual(int age);
}
```
package - repository.product
```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

     List<Product> findAllByPriceGreaterThanEqual(double price);
}
```

## Step 4
We'll start by setting up 2 configuration classes – one for the User and the other for the Product.

In each one of this configuration classes, we'll need to define the following interfaces:
- DataSource;
- EntityManagerFactory;
- TransactionManager.
```java
@Slf4j
@Setter
@Configuration
@EnableJpaRepositories(basePackages = "com.github.camelya58.multipledatabases.repository.user",
        entityManagerFactoryRef = "userEntityManager",
        transactionManagerRef = "userTransactionManager"
)
@ConfigurationProperties(prefix = "app.db.user")
@RequiredArgsConstructor
public class UserConfig {

    private static final String[] ENTITY_PACKAGES = {"com.github.camelya58.multipledatabases.model.user"};

    String url;
    String user;
    String password;
    String driver;
    Database database;
    String databasePlatform;
    boolean showSql;

    @PostConstruct
    public void init() {
        var pass = (password == null) || password.isEmpty() ? "" : "********";
        log.info("[DWH] url = [{}], user=[{}], password=[{}] driver=[{}], database=[{}], databasePlatform=[{}], " +
                        "showSql=[{}]",
                url, user, pass, driver, database, databasePlatform, showSql);
    }

    @Bean
    @Qualifier("user")
    public DataSource userDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(user);
        ds.setPassword(password);
        ds.setDriverClassName(driver);
        return ds;
    }

    @Bean
    @Qualifier("user")
    public LocalContainerEntityManagerFactoryBean userEntityManager(
            @Qualifier("user") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setDataSource(dataSource);
        entityManager.setPackagesToScan(ENTITY_PACKAGES);

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(database);
        vendorAdapter.setShowSql(showSql);
        vendorAdapter.setDatabasePlatform(databasePlatform);
        vendorAdapter.setGenerateDdl(false);

        entityManager.setJpaVendorAdapter(vendorAdapter);

        Properties properties = new Properties();
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.dialect", databasePlatform);
        properties.put("hibernate.connection.datasource", dataSource);

        entityManager.setJpaProperties(properties);
        return entityManager;
    }

    @Bean
    @Qualifier("user")
    public PlatformTransactionManager userTransactionManager(@Qualifier("user") DataSource dataSource) {

        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(userEntityManager(dataSource).getObject());
        return transactionManager;
    }
}
```
```java
@Slf4j
@Setter
@Configuration
@EnableJpaRepositories(basePackages = "com.github.camelya58.multipledatabases.repository.product",
        entityManagerFactoryRef = "productEntityManager",
        transactionManagerRef = "productTransactionManager"
)
@ConfigurationProperties(prefix = "app.db.product")
@RequiredArgsConstructor
public class ProductConfig {

    private static final String[] ENTITY_PACKAGES = {"com.github.camelya58.multipledatabases.model.product"};

    String url;
    String user;
    String password;
    String driver;
    Database database;
    String databasePlatform;
    boolean showSql;

    @PostConstruct
    public void init() {
        var pass = (password == null) || password.isEmpty() ? "" : "********";
        log.info("[DWH] url = [{}], user=[{}], password=[{}] driver=[{}], database=[{}], databasePlatform=[{}], " +
                        "showSql=[{}]",
                url, user, pass, driver, database, databasePlatform, showSql);
    }

    @Bean
    @Qualifier("product")
    public DataSource productDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(user);
        ds.setPassword(password);
        ds.setDriverClassName(driver);
        return ds;
    }

    @Bean
    @Qualifier("product")
    public LocalContainerEntityManagerFactoryBean productEntityManager(
            @Qualifier("product") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setDataSource(dataSource);
        entityManager.setPackagesToScan(ENTITY_PACKAGES);

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(database);
        vendorAdapter.setShowSql(showSql);
        vendorAdapter.setDatabasePlatform(databasePlatform);
        vendorAdapter.setGenerateDdl(false);

        entityManager.setJpaVendorAdapter(vendorAdapter);

        Properties properties = new Properties();
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.dialect", databasePlatform);
        properties.put("hibernate.connection.datasource", dataSource);

        entityManager.setJpaProperties(properties);
        return entityManager;
    }

    @Bean
    @Qualifier("product")
    public PlatformTransactionManager productTransactionManager(@Qualifier("product") DataSource dataSource) {

        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(productEntityManager(dataSource).getObject());
        return transactionManager;
    }
}
```

## Step 5
Create tables and fill them with testing data.

For H2 database we need to initialize the table and fill them with data anytime.
```java
@Component
public class UserDbInit {
    private static final String SQL_CREATE_USERS = "" +
            "create table users (" +
            "  id numeric," +
            "  name varchar(64)," +
            "  email varchar(64) not null," +
            "  age numeric" +
            ")";

    private static final String SQL_INSERT_USERS = "" +
            "insert into users (id, name, email, age) values " +
            "(1, 'Anna', 'anna@mail.ru', 30)," +
            "(2, 'Olga', 'olga@yandex.ru', 18)," +
            "(3, 'Igor', 'igor@rambler.ru', 15)," +
            "(4, 'Oleg',  'oleg@mail.ru', 20)," +
            "(5, 'Marina', 'marina@google.com', 25)";

    final DataSource dataSource;

    public UserDbInit(@Qualifier("user") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    void init() {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.execute(SQL_CREATE_USERS);
        template.execute(SQL_INSERT_USERS);
    }
}
```
For PostgreSQL database we need to initialize the table and fill them with data only once.
```java
@Component
public class ProductDbInit {
    private static final String SQL_CREATE_PRODUCTS = "" +
            "create table products (" +
            "  id numeric," +
            "  description varchar(64)," +
            "  price numeric" +
            ")";

    private static final String SQL_INSERT_PRODUCTS = "" +
            "insert into products (id, description, price) values " +
            "(1, 'book', 300.0)," +
            "(2, 'journal', 200.0)," +
            "(3, 'magazine', 150.0)," +
            "(4, 'text book', 100.0)," +
            "(5, 'postcard', 50.0)";

    final DataSource dataSource;

    public ProductDbInit(@Qualifier("product") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    void init() {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.execute(SQL_CREATE_PRODUCTS);
        template.execute(SQL_INSERT_PRODUCTS);
    }
}
```

## Step 6
Create configurations for swagger.
```java
@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/swagger-ui.html");
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.github.camelya58.multipledatabases.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Multiple databases")
                .description("Service demonstrates multiple databases")
                .build();
    }
}
```

## Step 7
Create rest-controller.
```java
@RestController
@RequiredArgsConstructor
public class DataController {

    private final UserRepository userRepo;
    private final ProductRepository productRepo;

    @GetMapping("/users")
    public List<User> getAllOlderThan(@RequestParam int age) {
        return userRepo.findAllByAgeGreaterThanEqual(age);
    }

    @GetMapping("/products")
    public List<Product> getAllExpensiveThan(@RequestParam double price) {
        return productRepo.findAllByPriceGreaterThanEqual(price);
    }
}
```

## Step 8
Run the project and go to: http://localhost:9070/.

Source - https://www.baeldung.com/spring-data-jpa-multiple-databases.

