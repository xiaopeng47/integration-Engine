package com.integration.engine.core;

import com.integration.engine.error.EngineError;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class EventContext {
    private final Object payload;
    private final Map<String, Object> attributes;
    private final Map<String, Object> variables;
    private final Map<String, Object> metadata;
    private final EngineError error;

    private EventContext(Object payload,
                         Map<String, Object> attributes,
                         Map<String, Object> variables,
                         Map<String, Object> metadata,
                         EngineError error) {
        this.payload = payload;
        this.attributes = Map.copyOf(attributes);
        this.variables = Map.copyOf(variables);
        this.metadata = Map.copyOf(metadata);
        this.error = error;
    }

    public static EventContext empty() {
        return new EventContext(null, Map.of(), Map.of(), Map.of(), null);
    }

    public Object payload() {
        return payload;
    }

    public Map<String, Object> attributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public Map<String, Object> variables() {
        return Collections.unmodifiableMap(variables);
    }

    public Map<String, Object> metadata() {
        return Collections.unmodifiableMap(metadata);
    }

    public EngineError error() {
        return error;
    }

    public EventContext withPayload(Object newPayload) {
        return new EventContext(newPayload, attributes, variables, metadata, error);
    }

    public EventContext withVariable(String key, Object value) {
        Objects.requireNonNull(key, "key must not be null");
        Map<String, Object> copy = new HashMap<>(variables);
        copy.put(key, value);
        return new EventContext(payload, attributes, copy, metadata, error);
    }

    public EventContext withMetadata(String key, Object value) {
        Objects.requireNonNull(key, "key must not be null");
        Map<String, Object> copy = new HashMap<>(metadata);
        copy.put(key, value);
        return new EventContext(payload, attributes, variables, copy, error);
    }

    public EventContext withError(EngineError newError) {
        return new EventContext(payload, attributes, variables, metadata, newError);
    }
}
