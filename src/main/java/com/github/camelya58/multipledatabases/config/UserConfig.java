package com.github.camelya58.multipledatabases.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Class UserConfig sets configurations and connection with database "user".
 *
 * @author Kamila Meshcheryakova
 * created 08.09.2020
 */
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
