package com.integration.engine.ir;

import java.util.Map;

public record NodeModel(String type, String name, Map<String, String> attributes) {
}
