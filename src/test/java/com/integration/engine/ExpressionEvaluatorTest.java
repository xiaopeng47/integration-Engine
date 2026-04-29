package com.integration.engine;

import com.integration.engine.core.EventContext;
import com.integration.engine.expression.ExpressionEvaluator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExpressionEvaluatorTest {

    @Test
    void shouldResolveWrappedExpressionsAndInterpolation() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        EventContext context = EventContext.empty()
                .withPayload("payload-x")
                .withVariable("k", "v")
                .withVariable("flag", true);

        assertEquals("payload-x", evaluator.resolve("#[payload]", context));
        assertEquals("v", evaluator.resolve("#[vars.k]", context));
        assertTrue((Boolean) evaluator.resolve("#[vars.flag == true]", context));
        assertEquals("hello v", evaluator.resolve("hello ${vars.k}", context));
    }
}
