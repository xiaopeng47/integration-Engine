package com.integration.engine.processor.builtin;

import com.integration.engine.core.EventContext;
import com.integration.engine.expression.ExpressionEvaluator;
import com.integration.engine.ir.NodeModel;
import com.integration.engine.processor.Processor;
import com.integration.engine.runtime.support.ExecutionBridge;

public class SetVariableProcessor implements Processor {

    @Override
    public EventContext process(EventContext context,
                                NodeModel node,
                                ExecutionBridge executionBridge,
                                ExpressionEvaluator evaluator) {
        String name = node.attributes().getOrDefault("variableName", "var");
        String rawValue = node.attributes().getOrDefault("value", "");
        Object value = evaluator.resolve(rawValue, context);
        return context.withVariable(name, value);
    }
}
