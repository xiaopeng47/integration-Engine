package com.integration.engine.ir;

import java.util.List;
import java.util.Map;

public record NodeModel(String type, String name, Map<String, String> attributes, List<NodeModel> children) {

    public NodeModel {
        attributes = Map.copyOf(attributes);
        children = List.copyOf(children);
    }

    public static NodeModel of(String type, String name, Map<String, String> attributes) {
        return new NodeModel(type, name, attributes, List.of());
    }
}
