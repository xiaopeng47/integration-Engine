package com.integration.engine.processor.builtin.error;

import com.integration.engine.core.EventContext;
import com.integration.engine.expression.ExpressionEvaluator;
import com.integration.engine.ir.NodeModel;
import com.integration.engine.processor.Processor;
import com.integration.engine.runtime.support.ExecutionBridge;

import java.util.ArrayList;
import java.util.List;

public class TryProcessor implements Processor {

    @Override
    public EventContext process(EventContext context,
                                NodeModel node,
                                ExecutionBridge executionBridge,
                                ExpressionEvaluator evaluator) {
        List<NodeModel> body = new ArrayList<>();
        List<NodeModel> handlers = new ArrayList<>();

        for (NodeModel child : node.children()) {
            if ("on-error-continue".equals(child.type()) || "on-error-propagate".equals(child.type())) {
                handlers.add(child);
            } else {
                body.add(child);
            }
        }

        EventContext current = context;
        try {
            for (NodeModel child : body) {
                current = executionBridge.executeInline(child, current);
            }
            return current;
        } catch (Exception e) {
            return handleException(current, handlers, e, executionBridge);
        }
    }

    private EventContext handleException(EventContext context,
                                         List<NodeModel> handlers,
                                         Exception exception,
                                         ExecutionBridge executionBridge) {
        for (NodeModel handler : handlers) {
            EventContext handled = context.withVariable("error.message", exception.getMessage())
                    .withVariable("error.type", exception.getClass().getSimpleName());

            for (NodeModel child : handler.children()) {
                handled = executionBridge.executeInline(child, handled);
            }

            if ("on-error-continue".equals(handler.type())) {
                return handled;
            }
            if ("on-error-propagate".equals(handler.type())) {
                throw new IllegalStateException("Propagated from try scope", exception);
            }
        }
        throw new IllegalStateException("Unhandled exception in try scope", exception);
    }
}
