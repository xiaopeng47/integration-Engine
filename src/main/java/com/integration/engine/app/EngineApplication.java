package com.integration.engine.app;

import com.integration.engine.connector.http.impl.JdkHttpConnector;
import com.integration.engine.core.EventContext;
import com.integration.engine.ir.AppModel;
import com.integration.engine.parser.MuleXmlParser;
import com.integration.engine.processor.ProcessorRegistry;
import com.integration.engine.processor.builtin.ChoiceProcessor;
import com.integration.engine.processor.builtin.FlowRefProcessor;
import com.integration.engine.processor.builtin.ForEachProcessor;
import com.integration.engine.processor.builtin.LoggerProcessor;
import com.integration.engine.processor.builtin.SetVariableProcessor;
import com.integration.engine.processor.builtin.error.FailProcessor;
import com.integration.engine.processor.builtin.error.TryProcessor;
import com.integration.engine.processor.builtin.http.HttpRequestProcessor;
import com.integration.engine.runtime.EngineRuntime;
import com.integration.engine.runtime.deploy.HttpListenerDeployer;

import java.net.http.HttpClient;
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
        registry.register("flow-ref", new FlowRefProcessor());
        registry.register("for-each", new ForEachProcessor());
        registry.register("choice", new ChoiceProcessor());
        registry.register("http-request", new HttpRequestProcessor(new JdkHttpConnector(HttpClient.newHttpClient())));
        registry.register("try", new TryProcessor());
        registry.register("fail", new FailProcessor());
        registry.register("http-listener", (context, node, bridge, evaluator) -> context);

        EngineRuntime runtime = new EngineRuntime(model, registry);

        new HttpListenerDeployer().deploy(model, runtime);

        EventContext context = EventContext.empty().withPayload("bootstrap");
        runtime.execute(flowName, context);
    }
}
