package com.integration.engine.ir;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record AppModel(Map<String, FlowModel> flows) {

    public Optional<FlowModel> flow(String name) {
        return Optional.ofNullable(flows.get(name));
    }

    public static AppModel of(List<FlowModel> flowModels) {
        return new AppModel(flowModels.stream().collect(java.util.stream.Collectors.toMap(FlowModel::name, f -> f)));
    }
}
