package com.integration.engine.processor.builtin;

import com.integration.engine.core.EventContext;
import com.integration.engine.expression.ExpressionEvaluator;
import com.integration.engine.ir.NodeModel;
import com.integration.engine.processor.Processor;
import com.integration.engine.runtime.support.ExecutionBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerProcessor implements Processor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerProcessor.class);

    @Override
    public EventContext process(EventContext context,
                                NodeModel node,
                                ExecutionBridge executionBridge,
                                ExpressionEvaluator evaluator) {
        String rawMessage = node.attributes().getOrDefault("message", "[logger] no message configured");
        String message = evaluator.resolveToString(rawMessage, context);
        LOGGER.info("{} | payload={} | vars={}", message, context.payload(), context.variables());
        return context;
    }
}
