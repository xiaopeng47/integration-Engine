package com.integration.engine.connector.http;

import java.util.Map;

public record HttpResult(int status, Map<String, String> headers, String body) {
}
