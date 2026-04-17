package com.integration.engine.runtime;

import com.integration.engine.core.EventContext;
import com.integration.engine.ir.AppModel;
import com.integration.engine.ir.FlowModel;
import com.integration.engine.ir.NodeModel;
import com.integration.engine.processor.ProcessorRegistry;

public class EngineRuntime {
    private final AppModel appModel;
    private final ProcessorRegistry processorRegistry;

    public EngineRuntime(AppModel appModel, ProcessorRegistry processorRegistry) {
        this.appModel = appModel;
        this.processorRegistry = processorRegistry;
    }

    public EventContext execute(String flowName, EventContext context) {
        FlowModel flow = appModel.flow(flowName)
                .orElseThrow(() -> new IllegalArgumentException("Flow not found: " + flowName));

        EventContext current = context;
        for (NodeModel node : flow.nodes()) {
            current = processorRegistry.find(node.type())
                    .map(processor -> processor.process(current, node))
                    .orElse(current);
        }
        return current;
    }
}
