package com.integration.engine.runtime;

import com.integration.engine.core.EventContext;
import com.integration.engine.expression.ExpressionEvaluator;
import com.integration.engine.ir.AppModel;
import com.integration.engine.ir.FlowModel;
import com.integration.engine.ir.NodeModel;
import com.integration.engine.processor.Processor;
import com.integration.engine.processor.ProcessorRegistry;
import com.integration.engine.runtime.support.ExecutionBridge;

public class EngineRuntime implements ExecutionBridge {

    private final AppModel appModel;
    private final ProcessorRegistry processorRegistry;
    private final ExpressionEvaluator expressionEvaluator;

    public EngineRuntime(AppModel appModel, ProcessorRegistry processorRegistry) {
        this(appModel, processorRegistry, new ExpressionEvaluator());
    }

    public EngineRuntime(AppModel appModel,
                         ProcessorRegistry processorRegistry,
                         ExpressionEvaluator expressionEvaluator) {
        this.appModel = appModel;
        this.processorRegistry = processorRegistry;
        this.expressionEvaluator = expressionEvaluator;
    }

    @Override
    public EventContext executeFlow(String flowName, EventContext context) {
        FlowModel flow = appModel.flow(flowName)
                .orElseThrow(() -> new IllegalArgumentException("Flow not found: " + flowName));

        EventContext current = context;
        for (NodeModel node : flow.nodes()) {
            current = executeNode(current, node);
        }
        return current;
    }

    @Override
    public EventContext executeInline(NodeModel node, EventContext context) {
        return executeNode(context, node);
    }

    public EventContext execute(String flowName, EventContext context) {
        return executeFlow(flowName, context);
    }

    private EventContext executeNode(EventContext context, NodeModel node) {
        Processor processor = processorRegistry.find(node.type())
                .orElseThrow(() -> new IllegalStateException("No processor registered for type: " + node.type()));
        return processor.process(context, node, this, expressionEvaluator);
    }
}