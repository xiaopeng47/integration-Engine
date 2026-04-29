package com.integration.engine.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProcessorRegistry {
    private final Map<String, Processor> processors = new HashMap<>();

    public void register(String type, Processor processor) {
        processors.put(type, processor);
    }

    public Optional<Processor> find(String type) {
        return Optional.ofNullable(processors.get(type));
    }
}
