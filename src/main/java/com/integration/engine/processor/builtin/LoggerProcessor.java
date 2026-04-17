package com.integration.engine.processor.builtin;

import com.integration.engine.core.EventContext;
import com.integration.engine.ir.NodeModel;
import com.integration.engine.processor.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerProcessor implements Processor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerProcessor.class);

    @Override
    public EventContext process(EventContext context, NodeModel node) {
        String message = node.attributes().getOrDefault("message", "[logger] no message configured");
        LOGGER.info("{} | payload={} | vars={}", message, context.payload(), context.variables());
        return context;
    }
}
