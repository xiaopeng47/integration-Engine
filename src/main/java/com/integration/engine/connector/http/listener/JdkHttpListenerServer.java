package com.integration.engine.connector.http.listener;

import com.integration.engine.core.EventContext;
import com.integration.engine.runtime.EngineRuntime;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JdkHttpListenerServer implements HttpListenerServer {
    private final HttpServer server;
    private final ExecutorService executorService;

    public JdkHttpListenerServer(EngineRuntime runtime,
                                 String path,
                                 String flowName,
                                 int port) {
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create HTTP listener server", e);
        }

        this.executorService = Executors.newCachedThreadPool();
        this.server.setExecutor(executorService);
        this.server.createContext(path, exchange -> handle(exchange, runtime, flowName));
    }

    @Override
    public void start() {
        server.start();
    }

    @Override
    public int port() {
        return server.getAddress().getPort();
    }

    @Override
    public void close() {
        server.stop(0);
        executorService.shutdownNow();
    }

    private void handle(HttpExchange exchange, EngineRuntime runtime, String flowName) throws IOException {
        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        String requestBody = new String(requestBytes, StandardCharsets.UTF_8);

        EventContext context = EventContext.empty()
                .withPayload(requestBody)
                .withVariable("http.method", exchange.getRequestMethod())
                .withVariable("http.path", exchange.getRequestURI().getPath());

        String response;
        int status;
        try {
            EventContext output = runtime.execute(flowName, context);
            response = output.payload() == null ? "" : output.payload().toString();
            Object statusVar = output.variables().get("http.status");
            status = statusVar instanceof Number num ? num.intValue() : 200;
        } catch (Exception e) {
            response = e.getMessage() == null ? "Internal Error" : e.getMessage();
            status = 500;
        }

        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, responseBytes.length);
        exchange.getResponseBody().write(responseBytes);
        exchange.close();
    }
}
