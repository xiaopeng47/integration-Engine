package com.integration.engine.app;

import com.integration.engine.core.EventContext;
import com.integration.engine.ir.AppModel;
import com.integration.engine.parser.MuleXmlParser;
import com.integration.engine.processor.ProcessorRegistry;
import com.integration.engine.processor.builtin.LoggerProcessor;
import com.integration.engine.processor.builtin.SetVariableProcessor;
import com.integration.engine.runtime.EngineRuntime;

import java.nio.file.Path;

public final class EngineApplication {

    private EngineApplication() {
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar mule-replacement-engine.jar <app-xml-path> <flow-name>");
            return;
        }

        Path appXml = Path.of(args[0]);
        String flowName = args[1];

        AppModel model = new MuleXmlParser().parse(appXml);

        ProcessorRegistry registry = new ProcessorRegistry();
        registry.register("logger", new LoggerProcessor());
        registry.register("set-variable", new SetVariableProcessor());

        EngineRuntime runtime = new EngineRuntime(model, registry);
        EventContext context = EventContext.empty().withPayload("bootstrap");
        runtime.execute(flowName, context);
    }
}
