package com.integration.engine.runtime.support;

import com.integration.engine.core.EventContext;
import com.integration.engine.ir.NodeModel;

public interface ExecutionBridge {
    EventContext executeFlow(String flowName, EventContext context);

    EventContext executeInline(NodeModel node, EventContext context);
}