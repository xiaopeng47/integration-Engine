package com.integration.engine.processor.builtin;

import com.integration.engine.core.EventContext;
import com.integration.engine.expression.ExpressionEvaluator;
import com.integration.engine.ir.NodeModel;
import com.integration.engine.processor.Processor;
import com.integration.engine.runtime.support.ExecutionBridge;

public class FlowRefProcessor implements Processor {

    @Override
    public EventContext process(EventContext context,
                                NodeModel node,
                                ExecutionBridge executionBridge,
                                ExpressionEvaluator evaluator) {
        String targetFlow = node.attributes().get("name");
        if (targetFlow == null || targetFlow.isBlank()) {
            return context;
        }
        return executionBridge.executeFlow(targetFlow, context);
    }
}
