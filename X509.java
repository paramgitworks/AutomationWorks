import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.apache.wss4j.dom.WSSecSignature;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.security.KeyStore;

public class WSRequest {
    public static void main(String[] args) throws Exception {
        // Path to JKS and Password
        String jksPath = "/path/to/your.jks";
        String keystorePassword = "yourKeystorePassword";

        // Path to your XML file
        String xmlFilePath = "/path/to/your-soap-body.xml";

        // Load the KeyStore
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream(jksPath), keystorePassword.toCharArray());

        // Get the default alias
        String keyAlias = keyStore.aliases().nextElement();

        // Parse the XML file into a Document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document soapDocument = builder.parse(new File(xmlFilePath));

        // Add WS-Security Header
        WSSecHeader secHeader = new WSSecHeader();
        secHeader.insertSecurityHeader(soapDocument);

        // Sign the SOAP Document
        WSSecSignature signature = new WSSecSignature();
        signature.setUserInfo(keyAlias, keystorePassword);
        signature.build(soapDocument, keyStore, secHeader);

        // Convert Document back to String for REST Assured
        StringWriter writer = new StringWriter();
        javax.xml.transform.Transformer transformer = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
        transformer.transform(new javax.xml.transform.dom.DOMSource(soapDocument), new javax.xml.transform.stream.StreamResult(writer));

        // Send SOAP Request with Rest Assured
        RequestSpecification request = RestAssured.given();
        request.body(writer.toString())
                .header("Content-Type", "text/xml")
                .post("https://your.api.endpoint");
    }
}
