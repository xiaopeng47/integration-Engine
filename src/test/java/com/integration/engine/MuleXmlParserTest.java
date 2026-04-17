package com.integration.engine;

import com.integration.engine.ir.AppModel;
import com.integration.engine.parser.MuleXmlParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MuleXmlParserTest {

    @Test
    void shouldParseFlowAndNodes() throws Exception {
        String xml = """
                <mule xmlns=\"http://www.mulesoft.org/schema/mule/core\"
                      xmlns:http=\"http://www.mulesoft.org/schema/mule/http\"
                      xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">
                    <flow name=\"demoFlow\">
                        <set-variable variableName=\"foo\" value=\"bar\" doc:name=\"set foo\"/>
                        <logger message=\"hello\" doc:name=\"log\"/>
                    </flow>
                </mule>
                """;
        Path file = Files.createTempFile("mule-app", ".xml");
        Files.writeString(file, xml);

        AppModel appModel = new MuleXmlParser().parse(file);

        assertTrue(appModel.flow("demoFlow").isPresent());
        assertEquals(2, appModel.flow("demoFlow").orElseThrow().nodes().size());
    }
}
