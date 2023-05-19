package com.sqli.generators;

import de.hybris.platform.core.model.product.ProductModel;

public interface IGenerator {
    String generate(ProductModel product) throws Exception;
}
