package com.sqli.service;

public interface IHttpClient {
    String post(String url, String apiKey, String body) throws Exception;
}
