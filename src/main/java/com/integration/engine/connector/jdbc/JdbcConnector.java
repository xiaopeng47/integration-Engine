package com.integration.engine.connector.jdbc;

import java.util.List;
import java.util.Map;

public interface JdbcConnector {
    int update(String sql, List<Object> params);

    List<Map<String, Object>> query(String sql, List<Object> params);
}
