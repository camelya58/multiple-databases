package com.github.camelya58.multipledatabases.model.user;

import lombok.Data;

import javax.persistence.*;

/**
 * Class User represents an entity from database "testdb" table "users".
 *
 * @author Kamila Meshcheryakova
 * created 08.09.2020
 */
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
