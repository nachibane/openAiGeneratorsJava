package com.sqli.service.impl;

import com.sqli.service.ProductDescriptionService;
import com.sqli.generators.ProductDescriptionGenerator;

public class ProductDescriptionServiceImpl implements ProductDescriptionService {

    private ProductDescriptionGenerator generator;

    // Add a setter for Spring to inject the generator
    public void setGenerator(ProductDescriptionGenerator generator) {
        this.generator = generator;
    }

    @Override
    public String generateProductDescription(String productName, String features) throws Exception {
        return generator.generateDescription(productName, features);
    }
}
