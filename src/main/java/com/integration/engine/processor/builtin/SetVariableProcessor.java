package com.integration.engine.processor.builtin;

import com.integration.engine.core.EventContext;
import com.integration.engine.ir.NodeModel;
import com.integration.engine.processor.Processor;

public class SetVariableProcessor implements Processor {

    @Override
    public EventContext process(EventContext context, NodeModel node) {
        String name = node.attributes().getOrDefault("variableName", "var");
        String value = node.attributes().getOrDefault("value", "");
        return context.withVariable(name, value);
    }
}
