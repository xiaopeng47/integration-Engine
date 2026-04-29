package com.integration.engine.connector.http.impl;

import com.integration.engine.connector.http.HttpConnector;
import com.integration.engine.connector.http.HttpRequest;
import com.integration.engine.connector.http.HttpResult;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class JdkHttpConnector implements HttpConnector {
    private final HttpClient httpClient;

    public JdkHttpConnector(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public HttpResult request(HttpRequest request) {
        try {
            var builder = java.net.http.HttpRequest.newBuilder()
                    .uri(URI.create(request.url()))
                    .timeout(Duration.ofSeconds(30));

            if (request.headers() != null) {
                request.headers().forEach(builder::header);
            }

            String body = request.body() == null ? "" : request.body();
            builder.method(request.method(), BodyPublishers.ofString(body));

            var response = httpClient.send(builder.build(), BodyHandlers.ofString());
            Map<String, String> headers = new HashMap<>();
            response.headers().map().forEach((k, v) -> headers.put(k, String.join(",", v)));
            return new HttpResult(response.statusCode(), headers, response.body());
        } catch (Exception e) {
            throw new IllegalStateException("HTTP request failed", e);
        }
    }
}
