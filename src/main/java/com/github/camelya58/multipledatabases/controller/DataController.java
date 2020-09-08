package com.github.camelya58.multipledatabases.controller;

import com.github.camelya58.multipledatabases.model.product.Product;
import com.github.camelya58.multipledatabases.model.user.User;
import com.github.camelya58.multipledatabases.repository.product.ProductRepository;
import com.github.camelya58.multipledatabases.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Class DataController represents a simple REST-controller to receive lists of objects by given params.
 *
 * @author Kamila Meshcheryakova
 * created 08.09.2020
 */
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
