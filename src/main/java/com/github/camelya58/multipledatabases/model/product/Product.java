package com.github.camelya58.multipledatabases.model.product;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Class Product represents an entity from database "testdb" table "products".
 *
 * @author Kamila Meshcheryakova
 * created 08.09.2020
 */
@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    private int id;

    private String description;

    private double price;
}
