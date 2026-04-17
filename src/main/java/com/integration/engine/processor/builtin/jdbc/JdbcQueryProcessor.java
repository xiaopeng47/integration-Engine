package com.integration.engine.processor.builtin.jdbc;

import com.integration.engine.connector.jdbc.JdbcConnector;
import com.integration.engine.core.EventContext;
import com.integration.engine.expression.ExpressionEvaluator;
import com.integration.engine.ir.NodeModel;
import com.integration.engine.processor.Processor;
import com.integration.engine.runtime.support.ExecutionBridge;

import java.util.List;

public class JdbcQueryProcessor implements Processor {
    private final JdbcConnector jdbcConnector;

    public JdbcQueryProcessor(JdbcConnector jdbcConnector) {
        this.jdbcConnector = jdbcConnector;
    }

    @Override
    public EventContext process(EventContext context,
                                NodeModel node,
                                ExecutionBridge executionBridge,
                                ExpressionEvaluator evaluator) {
        if (jdbcConnector == null) {
            throw new IllegalStateException("No JdbcConnector configured for jdbc-query processor");
        }
        String sql = evaluator.resolveToString(node.attributes().get("sql"), context);
        var rows = jdbcConnector.query(sql, List.of());
        return context.withPayload(rows)
                .withVariable("jdbc.rowCount", rows.size());
    }
}
