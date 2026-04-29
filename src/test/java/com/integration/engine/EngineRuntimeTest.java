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
import com.integration.engine.processor.builtin.error.FailProcessor;
import com.integration.engine.processor.builtin.error.TryProcessor;
import com.integration.engine.runtime.EngineRuntime;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        NodeModel when = new NodeModel("when", "w", Map.of("expression", "#[vars.hit == 'YES']"), List.of(trueBranchSet));
        NodeModel otherwiseSet = NodeModel.of("set-variable", "fail", Map.of("variableName", "result", "value", "fail"));
        NodeModel otherwise = new NodeModel("otherwise", "o", Map.of(), List.of(otherwiseSet));
        NodeModel choice = new NodeModel("choice", "c", Map.of(), List.of(when, otherwise));

        NodeModel seed = NodeModel.of("set-variable", "seed", Map.of("variableName", "hit", "value", "YES"));
        NodeModel eachSet = NodeModel.of("set-variable", "fromEach", Map.of("variableName", "lastItem", "value", "#[payload]"));
        NodeModel each = new NodeModel("for-each", "each", Map.of("collection", "#[payload]", "itemVariableName", "item"), List.of(eachSet));
        FlowModel flow = new FlowModel("f", List.of(seed, choice, each));

        AppModel appModel = AppModel.of(List.of(flow));
        ProcessorRegistry registry = new ProcessorRegistry();
        registry.register("set-variable", new SetVariableProcessor());
        registry.register("choice", new ChoiceProcessor());
        registry.register("for-each", new ForEachProcessor());

        EngineRuntime runtime = new EngineRuntime(appModel, registry);
        EventContext output = runtime.execute("f", EventContext.empty().withPayload(List.of("A", "B", "C")));

        assertEquals("ok", output.variables().get("result"));
        assertEquals("C", output.variables().get("lastItem"));
        assertEquals(2, output.variables().get("itemIndex"));
    }

    @Test
    void shouldHandleTryOnErrorContinue() {
        NodeModel fail = NodeModel.of("fail", "boom", Map.of("message", "oops"));
        NodeModel recover = NodeModel.of("set-variable", "recover", Map.of("variableName", "handled", "value", "true"));
        NodeModel onContinue = new NodeModel("on-error-continue", "continue", Map.of(), List.of(recover));
        NodeModel tryNode = new NodeModel("try", "try", Map.of(), List.of(fail, onContinue));

        AppModel appModel = AppModel.of(List.of(new FlowModel("f", List.of(tryNode))));
        ProcessorRegistry registry = new ProcessorRegistry();
        registry.register("try", new TryProcessor());
        registry.register("fail", new FailProcessor());
        registry.register("set-variable", new SetVariableProcessor());

        EngineRuntime runtime = new EngineRuntime(appModel, registry);
        EventContext output = runtime.execute("f", EventContext.empty());

        assertEquals("true", output.variables().get("handled"));
        assertEquals("oops", output.variables().get("error.message"));
    }

    @Test
    void shouldHandleTryOnErrorPropagate() {
        NodeModel fail = NodeModel.of("fail", "boom", Map.of("message", "oops"));
        NodeModel onPropagate = new NodeModel("on-error-propagate", "prop", Map.of(), List.of());
        NodeModel tryNode = new NodeModel("try", "try", Map.of(), List.of(fail, onPropagate));

        AppModel appModel = AppModel.of(List.of(new FlowModel("f", List.of(tryNode))));
        ProcessorRegistry registry = new ProcessorRegistry();
        registry.register("try", new TryProcessor());
        registry.register("fail", new FailProcessor());

        EngineRuntime runtime = new EngineRuntime(appModel, registry);

        assertThrows(IllegalStateException.class, () -> runtime.execute("f", EventContext.empty()));
    }
}