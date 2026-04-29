package com.integration.engine.processor.builtin;

import com.integration.engine.core.EventContext;
import com.integration.engine.expression.ExpressionEvaluator;
import com.integration.engine.ir.NodeModel;
import com.integration.engine.processor.Processor;
import com.integration.engine.runtime.support.ExecutionBridge;

import java.util.Collection;

public class ForEachProcessor implements Processor {

    @Override
    public EventContext process(EventContext context,
                                NodeModel node,
                                ExecutionBridge executionBridge,
                                ExpressionEvaluator evaluator) {
        Object collectionObj = evaluator.resolve(node.attributes().getOrDefault("collection", "#[payload]"), context);
        if (!(collectionObj instanceof Collection<?> collection)) {
            return context;
        }

        EventContext current = context;
        String itemVar = node.attributes().getOrDefault("itemVariableName", "item");
        int index = 0;
        for (Object item : collection) {
            EventContext perItem = current.withVariable(itemVar, item)
                    .withVariable(itemVar + "Index", index)
                    .withPayload(item);
            for (NodeModel child : node.children()) {
                perItem = executionBridge.executeInline(child, perItem);
            }
            current = perItem;
            index++;
        }
        return current;
    }
}