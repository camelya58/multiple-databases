package com.github.camelya58.multipledatabases.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * Class UserDbInit creates a table "users" and fills with data.
 *
 * @author Kamila Meshcheryakova
 * created 08.09.2020
 */
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
