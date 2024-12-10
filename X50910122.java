import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class ModifyKeyInfo {
    public static void main(String[] args) throws Exception {
        // Load the XML into a Document
        String xml = """
                <Envelope xmlns="http://schemas.xmlsoap.org/soap/envelope/">
                    <Header>
                        <Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
                            <KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
                                <ds:Dummy>Existing KeyInfo</ds:Dummy>
                            </KeyInfo>
                        </Security>
                    </Header>
                    <Body>
                        <Request>Sample Request</Request>
                    </Body>
                </Envelope>
                """;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true); // Important for working with namespaces
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new java.io.ByteArrayInputStream(xml.getBytes()));

        // Locate the existing KeyInfo node
        NodeList keyInfoList = document.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "KeyInfo");
        if (keyInfoList.getLength() > 0) {
            Node keyInfoNode = keyInfoList.item(0);

            // Remove the existing KeyInfo node
            Node parent = keyInfoNode.getParentNode();
            parent.removeChild(keyInfoNode);

            // Create a new KeyInfo node
            Element newKeyInfo = document.createElementNS("http://www.w3.org/2000/09/xmldsig#", "ds:KeyInfo");

            Element securityTokenReference = document.createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse:SecurityTokenReference");
            Element x509IssuerSerial = document.createElementNS("http://www.w3.org/2000/09/xmldsig#", "ds:X509IssuerSerial");

            Element x509IssuerName = document.createElementNS("http://www.w3.org/2000/09/xmldsig#", "ds:X509IssuerName");
            x509IssuerName.setTextContent("CN=YourIssuerName, O=YourOrg, C=YourCountry");

            Element x509SerialNumber = document.createElementNS("http://www.w3.org/2000/09/xmldsig#", "ds:X509SerialNumber");
            x509SerialNumber.setTextContent("123456789");

            // Build the structure
            x509IssuerSerial.appendChild(x509IssuerName);
            x509IssuerSerial.appendChild(x509SerialNumber);
            securityTokenReference.appendChild(x509IssuerSerial);
            newKeyInfo.appendChild(securityTokenReference);

            // Append the new KeyInfo node to the parent
            parent.appendChild(newKeyInfo);
        }

        // Convert the Document back to a String for validation
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));

        // Print the modified XML
        System.out.println(writer.toString());
    }
}
