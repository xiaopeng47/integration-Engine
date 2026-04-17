package com.integration.engine.processor;

import com.integration.engine.core.EventContext;
import com.integration.engine.ir.NodeModel;

public interface Processor {
    EventContext process(EventContext context, NodeModel node);
}
