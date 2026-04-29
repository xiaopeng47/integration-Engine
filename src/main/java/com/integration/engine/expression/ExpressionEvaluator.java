package com.integration.engine.expression;

import com.integration.engine.core.EventContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionEvaluator {

    private static final Pattern WRAPPED = Pattern.compile("^#\\[(.*)]$");

    public String resolveToString(String raw, EventContext context) {
        Object value = resolve(raw, context);
        return value == null ? null : String.valueOf(value);
    }

    public Object resolve(String raw, EventContext context) {
        if (raw == null) {
            return null;
        }

        Matcher matcher = WRAPPED.matcher(raw.trim());
        if (!matcher.matches()) {
            return interpolate(raw, context);
        }

        String expression = matcher.group(1).trim();
        if (expression.startsWith("vars.")) {
            return context.variables().get(expression.substring("vars.".length()));
        }
        if ("payload".equals(expression)) {
            return context.payload();
        }
        if (expression.startsWith("attributes.")) {
            return context.attributes().get(expression.substring("attributes.".length()));
        }
        return raw;
    }

    private String interpolate(String raw, EventContext context) {
        String result = raw;
        for (var entry : context.variables().entrySet()) {
            result = result.replace("${vars." + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        return result;
    }
}
