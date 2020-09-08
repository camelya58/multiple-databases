package com.github.camelya58.multipledatabases.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * Class ProductDbInit creates a table "products" and fills with data.
 *
 * @author Kamila Meshcheryakova
 * created 08.09.2020
 */
//@Component
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
