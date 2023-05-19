package com.sqli.service.impl;

import com.sqli.service.IHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClientImpl implements IHttpClient {

    /**
     * Sends an API request to generate the product description.
     *
     * @param url    The API endpoint URL.
     * @param apiKey The API key for authentication.
     * @param body   The request body.
     * @return The response received from the API.
     * @throws Exception If an error occurs during the API request.
     */
    @Override
    public String post(String url, String apiKey, String body) throws Exception {
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
