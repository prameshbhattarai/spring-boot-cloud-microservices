package com.productservice.controller;

import com.productservice.entity.Product;
import com.productservice.service.ProductService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/products")
@Log4j2
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProduct();
        if (!products.isEmpty()) {
            log.info("Found {} products", products.size());
            return new ResponseEntity<List<Product>>(products, HttpStatus.OK);
        }
        log.error("Unable to find products");
        return new ResponseEntity<List<Product>>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(params = "category")
    public ResponseEntity<List<Product>> getAllProductsByCategory(@RequestParam("category") String category) {
        List<Product> products = productService.getAllProductByCategory(category);
        if (!products.isEmpty()) {
            log.info("Found {} products by categories", products.size());
            return new ResponseEntity<List<Product>>(products, HttpStatus.OK);
        }
        log.error("Unable to find products by categories");
        return new ResponseEntity<List<Product>>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") Long id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            log.info("Found product by id: {}", id);
            return new ResponseEntity<>(product, HttpStatus.OK);
        }
        log.error("Unable to find products by id: {}", id);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(params = "name")
    public ResponseEntity<List<Product>> getAllProductsByProductName(@RequestParam("name") String name) {
        List<Product> products = productService.getAllProductByName(name);
        if (!products.isEmpty()) {
            log.info("Found products by name: {}", name);
            return new ResponseEntity<List<Product>>(products, HttpStatus.OK);
        }
        log.error("Unable find product by name: {}", name);
        return new ResponseEntity<List<Product>>(HttpStatus.NOT_FOUND);
    }
}
