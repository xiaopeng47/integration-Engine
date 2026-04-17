package com.integration.engine.runtime.deploy;

import com.integration.engine.connector.http.listener.HttpListenerServer;
import com.integration.engine.connector.http.listener.JdkHttpListenerServer;
import com.integration.engine.ir.AppModel;
import com.integration.engine.ir.FlowModel;
import com.integration.engine.ir.NodeModel;
import com.integration.engine.runtime.EngineRuntime;

import java.util.ArrayList;
import java.util.List;

public class HttpListenerDeployer {

    public List<HttpListenerServer> deploy(AppModel appModel, EngineRuntime runtime) {
        List<HttpListenerServer> servers = new ArrayList<>();
        for (FlowModel flow : appModel.flows().values()) {
            NodeModel listener = findListener(flow);
            if (listener == null) {
                continue;
            }

            String path = listener.attributes().getOrDefault("path", "/");
            int port = parsePort(listener.attributes().getOrDefault("port", "8080"));
            JdkHttpListenerServer server = new JdkHttpListenerServer(runtime, path, flow.name(), port);
            server.start();
            servers.add(server);
        }
        return servers;
    }

    private NodeModel findListener(FlowModel flow) {
        for (NodeModel node : flow.nodes()) {
            if ("http-listener".equals(node.type())) {
                return node;
            }
        }
        return null;
    }

    private int parsePort(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 8080;
        }
    }
}
