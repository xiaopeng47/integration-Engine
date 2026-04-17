package com.integration.engine.processor;

import com.integration.engine.core.EventContext;
import com.integration.engine.expression.ExpressionEvaluator;
import com.integration.engine.ir.NodeModel;
import com.integration.engine.runtime.support.ExecutionBridge;

public interface Processor {
    EventContext process(EventContext context, NodeModel node, ExecutionBridge executionBridge, ExpressionEvaluator evaluator);
}
