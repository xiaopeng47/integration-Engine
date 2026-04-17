package com.integration.engine.service;

import com.integration.engine.connector.http.HttpConnector;
import com.integration.engine.connector.jdbc.JdbcConnector;

public record EngineServices(HttpConnector httpConnector, JdbcConnector jdbcConnector) {

    public static EngineServices empty() {
        return new EngineServices(null, null);
    }
}
