package com.integration.engine.processor.builtin.error;

import com.integration.engine.core.EventContext;
import com.integration.engine.expression.ExpressionEvaluator;
import com.integration.engine.ir.NodeModel;
import com.integration.engine.processor.Processor;
import com.integration.engine.runtime.support.ExecutionBridge;

public class FailProcessor implements Processor {
    @Override
    public EventContext process(EventContext context,
                                NodeModel node,
                                ExecutionBridge executionBridge,
                                ExpressionEvaluator evaluator) {
        String message = evaluator.resolveToString(node.attributes().getOrDefault("message", "forced failure"), context);
        throw new IllegalStateException(message);
    }
}
