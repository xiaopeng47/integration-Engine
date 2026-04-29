package com.integration.engine.processor.builtin.http;

import com.integration.engine.connector.http.HttpConnector;
import com.integration.engine.connector.http.HttpRequest;
import com.integration.engine.connector.http.HttpResult;
import com.integration.engine.core.EventContext;
import com.integration.engine.expression.ExpressionEvaluator;
import com.integration.engine.ir.NodeModel;
import com.integration.engine.processor.Processor;
import com.integration.engine.runtime.support.ExecutionBridge;

import java.util.Map;

public class HttpRequestProcessor implements Processor {
    private final HttpConnector httpConnector;

    public HttpRequestProcessor(HttpConnector httpConnector) {
        this.httpConnector = httpConnector;
    }

    @Override
    public EventContext process(EventContext context,
                                NodeModel node,
                                ExecutionBridge executionBridge,
                                ExpressionEvaluator evaluator) {
        if (httpConnector == null) {
            throw new IllegalStateException("No HttpConnector configured for http-request processor");
        }

        String method = node.attributes().getOrDefault("method", "GET");
        String url = evaluator.resolveToString(node.attributes().get("url"), context);
        String body = evaluator.resolveToString(node.attributes().get("body"), context);

        HttpResult result = httpConnector.request(new HttpRequest(method, url, Map.of(), body));
        return context.withPayload(result.body())
                .withVariable("http.status", result.status());
    }
}
