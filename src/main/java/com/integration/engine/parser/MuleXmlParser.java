package com.integration.engine.parser;

import com.integration.engine.ir.AppModel;
import com.integration.engine.ir.FlowModel;
import com.integration.engine.ir.NodeModel;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MuleXmlParser {

    public AppModel parse(Path xmlPath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document document = factory.newDocumentBuilder().parse(xmlPath.toFile());
            Element root = document.getDocumentElement();

            NodeList children = root.getChildNodes();
            List<FlowModel> flows = new ArrayList<>();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (!(node instanceof Element element)) {
                    continue;
                }
                String localName = element.getLocalName();
                if (!"flow".equals(localName) && !"sub-flow".equals(localName)) {
                    continue;
                }

                String flowName = element.getAttribute("name");
                flows.add(new FlowModel(flowName, parseFlowNodes(element)));
            }
            return AppModel.of(flows);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse Mule app xml: " + xmlPath, e);
        }
    }

    private List<NodeModel> parseFlowNodes(Element flowElement) {
        List<NodeModel> nodes = new ArrayList<>();
        NodeList children = flowElement.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (!(node instanceof Element child)) {
                continue;
            }
            String type = normalizeType(child);
            String name = child.getAttribute("doc:name");
            nodes.add(new NodeModel(type, name == null ? "" : name, attributesOf(child)));
        }
        return nodes;
    }

    private String normalizeType(Element element) {
        String local = element.getLocalName();
        if (local == null) {
            return "unknown";
        }
        return switch (local) {
            case "logger" -> "logger";
            case "set-variable" -> "set-variable";
            default -> local;
        };
    }

    private Map<String, String> attributesOf(Element element) {
        Map<String, String> map = new HashMap<>();
        NamedNodeMap namedNodeMap = element.getAttributes();
        for (int i = 0; i < namedNodeMap.getLength(); i++) {
            Node item = namedNodeMap.item(i);
            map.put(item.getNodeName(), item.getNodeValue());
        }
        return map;
    }
}
