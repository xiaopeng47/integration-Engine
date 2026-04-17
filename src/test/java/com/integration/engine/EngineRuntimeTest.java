package com.integration.engine;

import com.integration.engine.core.EventContext;
import com.integration.engine.ir.AppModel;
import com.integration.engine.ir.FlowModel;
import com.integration.engine.ir.NodeModel;
import com.integration.engine.processor.ProcessorRegistry;
import com.integration.engine.processor.builtin.SetVariableProcessor;
import com.integration.engine.runtime.EngineRuntime;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EngineRuntimeTest {

    @Test
    void shouldExecuteRegisteredProcessors() {
        NodeModel setVar = new NodeModel("set-variable", "setVar", Map.of("variableName", "k", "value", "v"));
        FlowModel flow = new FlowModel("f1", List.of(setVar));
        AppModel appModel = AppModel.of(List.of(flow));

        ProcessorRegistry registry = new ProcessorRegistry();
        registry.register("set-variable", new SetVariableProcessor());

        EngineRuntime runtime = new EngineRuntime(appModel, registry);
        EventContext output = runtime.execute("f1", EventContext.empty());

        assertEquals("v", output.variables().get("k"));
    }
}
