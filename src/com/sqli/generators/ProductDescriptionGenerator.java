package com.sqli.generators;


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

public class ProductDescriptionGenerator {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

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
        String response = sendRequest(apiUrl, apiKey, body.toString());

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

    /**
     * Sends an API request to generate the product description.
     *
     * @param url    The API endpoint URL.
     * @param apiKey The API key for authentication.
     * @param body   The request body.
     * @return The response received from the API.
     * @throws Exception If an error occurs during the API request.
     */
    private String sendRequest(String url, String apiKey, String body) throws Exception {
        URL urlObj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        os.write(body.getBytes());
        os.flush();
        os.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();

        return response.toString();
    }
}