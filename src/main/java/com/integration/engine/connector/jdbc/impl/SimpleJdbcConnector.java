package com.integration.engine.connector.jdbc.impl;

import com.integration.engine.connector.jdbc.JdbcConnector;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleJdbcConnector implements JdbcConnector {
    private final DataSource dataSource;

    public SimpleJdbcConnector(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int update(String sql, List<Object> params) {
        try (var connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            bindParams(ps, params);
            return ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException("JDBC update failed", e);
        }
    }

    @Override
    public List<Map<String, Object>> query(String sql, List<Object> params) {
        try (var connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> rows = new ArrayList<>();
                int columnCount = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(rs.getMetaData().getColumnLabel(i), rs.getObject(i));
                    }
                    rows.add(row);
                }
                return rows;
            }
        } catch (Exception e) {
            throw new IllegalStateException("JDBC query failed", e);
        }
    }

    private void bindParams(PreparedStatement ps, List<Object> params) throws Exception {
        if (params == null) {
            return;
        }
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }
}
