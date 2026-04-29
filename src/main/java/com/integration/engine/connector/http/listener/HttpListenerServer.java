package com.integration.engine.connector.http.listener;

import java.io.Closeable;

public interface HttpListenerServer extends Closeable {
    void start();

    int port();
}
