package com.sqli.generators.impl;

import com.sqli.generators.IGenerator;
import com.sqli.service.IHttpClient;
import de.hybris.platform.core.model.product.ProductModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProductDescriptionGenerator implements IGenerator {
    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;
    private final IHttpClient httpClient;

    public ProductDescriptionGenerator(IHttpClient httpClient) {
        this.httpClient = httpClient;
    }
    /**
     * Generates a product description using the given product name and features.
     *
     * @param productName The name of the product.
     * @param features    The features of the product.
     * @return The generated product description.
     * @throws Exception If an error occurs during the generation process.
     */
    public String generateDescription(String productName, String features) throws Exception {
        String prompt = createPrompt(productName, features);

        JsonObject body = createRequestBody(prompt);
        String response = httpClient.post(apiUrl, apiKey, body.toString());;

        return parseResponse(response, prompt);
    }

    /**
     * Creates the prompt for generating the product description.
     *
     * @param productName The name of the product.
     * @param features    The features of the product.
     * @return The created prompt.
     */
    private String createPrompt(String productName, String features) {
        return "Product: " + productName + "\n\nFeatures: " + features + "\n\nDescription:";
    }

    /**
     * Creates the request body for the API request.
     *
     * @param prompt The prompt for generating the product description.
     * @return The created request body as a JsonObject.
     */
    private JsonObject createRequestBody(String prompt) {
        return Json.createObjectBuilder()
                .add("prompt", prompt)
                .add("max_tokens", 100)
                .add("temperature", 0)
                .add("n", 1)
                .build();
    }

    /**
     * Parses the API response to extract the generated product description.
     *
     * @param response The response received from the API.
     * @param prompt   The prompt used for generating the product description.
     * @return The generated product description.
     * @throws JSONException If an error occurs while parsing the JSON response.
     */
    private String parseResponse(String response, String prompt) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray choicesArray = jsonResponse.getJSONArray("choices");
        String text = choicesArray.getJSONObject(0).getString("text");
        return text.trim().replace(prompt, "");
    }

    @Override
    public String generate(ProductModel product) throws Exception {
        String productName = product.getName();
        String features = product.getFeatures().toString();// extract features from product
        return generateDescription(productName, features);
    }
}
