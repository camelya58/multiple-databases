package com.github.camelya58.multipledatabases.repository.product;

import com.github.camelya58.multipledatabases.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface ProductRepository sets connection with PostgreSQL database "testdb".
 *
 * @author Kamila Meshcheryakova
 * created 08.09.2020
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findAllByPriceGreaterThanEqual(double price);
}
