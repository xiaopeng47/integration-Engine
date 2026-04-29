package com.integration.engine;

import com.integration.engine.ir.AppModel;
import com.integration.engine.ir.NodeModel;
import com.integration.engine.parser.MuleXmlParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MuleXmlParserTest {

    @Test
    void shouldParseFlowAndNestedNodes() throws Exception {
        String xml = """
                <mule xmlns="http://www.mulesoft.org/schema/mule/core"
                      xmlns:http="http://www.mulesoft.org/schema/mule/http"
                      xmlns:doc="http://www.mulesoft.org/schema/mule/documentation"
                      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                    <flow name="demoFlow">
                        <http:listener path="/hello" port="8081"/>
                        <set-variable variableName="foo" value="bar" doc:name="set foo"/>
                        <choice>
                          <when expression="#[vars.foo]">
                            <logger message="hello ${vars.foo}" doc:name="log"/>
                          </when>
                        </choice>
                        <try>
                          <raise-error message="boom"/>
                          <on-error-continue>
                            <set-variable variableName="handled" value="1"/>
                          </on-error-continue>
                        </try>
                        <http:request method="GET" url="https://example.com" />
                    </flow>
                </mule>
                """;
        Path file = Files.createTempFile("mule-app", ".xml");
        Files.writeString(file, xml);

        AppModel appModel = new MuleXmlParser().parse(file);

        assertTrue(appModel.flow("demoFlow").isPresent());
        assertEquals(5, appModel.flow("demoFlow").orElseThrow().nodes().size());

        NodeModel listener = appModel.flow("demoFlow").orElseThrow().nodes().getFirst();
        assertEquals("http-listener", listener.type());

        NodeModel choice = appModel.flow("demoFlow").orElseThrow().nodes().get(2);
        assertEquals("choice", choice.type());
        assertEquals(1, choice.children().size());
        assertEquals("when", choice.children().getFirst().type());

        NodeModel tryNode = appModel.flow("demoFlow").orElseThrow().nodes().get(3);
        assertEquals("try", tryNode.type());
        assertEquals("fail", tryNode.children().getFirst().type());

        NodeModel httpRequest = appModel.flow("demoFlow").orElseThrow().nodes().get(4);
        assertEquals("http-request", httpRequest.type());
    }
}