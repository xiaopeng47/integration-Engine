package com.integration.engine;

import com.integration.engine.core.EventContext;
import com.integration.engine.ir.AppModel;
import com.integration.engine.ir.FlowModel;
import com.integration.engine.ir.NodeModel;
import com.integration.engine.processor.ProcessorRegistry;
import com.integration.engine.processor.builtin.ChoiceProcessor;
import com.integration.engine.processor.builtin.FlowRefProcessor;
import com.integration.engine.processor.builtin.ForEachProcessor;
import com.integration.engine.processor.builtin.SetVariableProcessor;
import com.integration.engine.runtime.EngineRuntime;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EngineRuntimeTest {

    @Test
    void shouldExecuteRegisteredProcessors() {
        NodeModel setVar = NodeModel.of("set-variable", "setVar", Map.of("variableName", "k", "value", "v"));
        FlowModel flow = new FlowModel("f1", List.of(setVar));
        AppModel appModel = AppModel.of(List.of(flow));

        ProcessorRegistry registry = new ProcessorRegistry();
        registry.register("set-variable", new SetVariableProcessor());

        EngineRuntime runtime = new EngineRuntime(appModel, registry);
        EventContext output = runtime.execute("f1", EventContext.empty());

        assertEquals("v", output.variables().get("k"));
    }

    @Test
    void shouldSupportFlowRefAndExpression() {
        NodeModel subSet = NodeModel.of("set-variable", "s", Map.of("variableName", "fromSub", "value", "#[payload]"));
        FlowModel subFlow = new FlowModel("sub", List.of(subSet));

        NodeModel ref = NodeModel.of("flow-ref", "ref", Map.of("name", "sub"));
        FlowModel mainFlow = new FlowModel("main", List.of(ref));

        AppModel appModel = AppModel.of(List.of(mainFlow, subFlow));
        ProcessorRegistry registry = new ProcessorRegistry();
        registry.register("flow-ref", new FlowRefProcessor());
        registry.register("set-variable", new SetVariableProcessor());

        EngineRuntime runtime = new EngineRuntime(appModel, registry);
        EventContext output = runtime.execute("main", EventContext.empty().withPayload("P"));

        assertEquals("P", output.variables().get("fromSub"));
    }

    @Test
    void shouldSupportChoiceAndForEach() {
        NodeModel trueBranchSet = NodeModel.of("set-variable", "ok", Map.of("variableName", "result", "value", "ok"));
        NodeModel when = new NodeModel("when", "w", Map.of("expression", "#[vars.hit]"), List.of(trueBranchSet));
        NodeModel otherwiseSet = NodeModel.of("set-variable", "fail", Map.of("variableName", "result", "value", "fail"));
        NodeModel otherwise = new NodeModel("otherwise", "o", Map.of(), List.of(otherwiseSet));
        NodeModel choice = new NodeModel("choice", "c", Map.of(), List.of(when, otherwise));

        NodeModel seed = NodeModel.of("set-variable", "seed", Map.of("variableName", "hit", "value", "#[payload]"));
        FlowModel flow = new FlowModel("f", List.of(seed, choice));

        AppModel appModel = AppModel.of(List.of(flow));
        ProcessorRegistry registry = new ProcessorRegistry();
        registry.register("set-variable", new SetVariableProcessor());
        registry.register("choice", new ChoiceProcessor());
        registry.register("for-each", new ForEachProcessor());

        EngineRuntime runtime = new EngineRuntime(appModel, registry);
        EventContext output = runtime.execute("f", EventContext.empty().withPayload(true));

        assertEquals("ok", output.variables().get("result"));
    }
}
