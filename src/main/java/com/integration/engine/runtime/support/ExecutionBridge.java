package com.integration.engine.runtime.support;

import com.integration.engine.core.EventContext;

public interface ExecutionBridge {
    EventContext executeFlow(String flowName, EventContext context);
}
