package com.integration.engine.processor.builtin;

import com.integration.engine.core.EventContext;
import com.integration.engine.expression.ExpressionEvaluator;
import com.integration.engine.ir.NodeModel;
import com.integration.engine.processor.Processor;
import com.integration.engine.runtime.support.ExecutionBridge;

public class ChoiceProcessor implements Processor {

    @Override
    public EventContext process(EventContext context,
                                NodeModel node,
                                ExecutionBridge executionBridge,
                                ExpressionEvaluator evaluator) {
        NodeModel otherwise = null;
        for (NodeModel branch : node.children()) {
            if ("when".equals(branch.type())) {
                String expr = branch.attributes().get("expression");
                Object value = evaluator.resolve(expr, context);
                if (value instanceof Boolean bool && bool) {
                    return executeBranch(context, branch, executionBridge);
                }
            }
            if ("otherwise".equals(branch.type())) {
                otherwise = branch;
            }
        }
        return otherwise == null ? context : executeBranch(context, otherwise, executionBridge);
    }

    private EventContext executeBranch(EventContext context, NodeModel branch, ExecutionBridge executionBridge) {
        EventContext current = context;
        for (NodeModel child : branch.children()) {
            current = executionBridge.executeFlow("__inline__", current.withMetadata("inline.node", child));
        }
        return current;
    }
}
