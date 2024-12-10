import javax.xml.soap.*;
import org.w3c.dom.*;

public class ReplaceKeyInfo {
    public static void main(String[] args) throws Exception {
        // Create SOAP Message
        SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPHeader header = envelope.getHeader();

        // Assume Security Header already exists
        SOAPElement security = header.addChildElement("Security", "wsse", 
                "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");

        // Add a placeholder KeyInfo (to simulate an existing KeyInfo node)
        SOAPElement existingKeyInfo = security.addChildElement("KeyInfo", "ds", "http://www.w3.org/2000/09/xmldsig#");
        existingKeyInfo.addTextNode("Placeholder KeyInfo");

        // Access the Document
        Document document = soapPart.getEnvelope().getOwnerDocument();

        // Locate and Remove Existing KeyInfo
        NodeList keyInfoList = document.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "KeyInfo");
        if (keyInfoList.getLength() > 0) {
            Node oldKeyInfo = keyInfoList.item(0); // Assume the first occurrence is the one to replace
            oldKeyInfo.getParentNode().removeChild(oldKeyInfo);
        }

        // Create New KeyInfo
        String DS_NAMESPACE = "http://www.w3.org/2000/09/xmldsig#";
        String WSSE_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";

        Element newKeyInfo = document.createElementNS(DS_NAMESPACE, "ds:KeyInfo");
        Element securityTokenReference = document.createElementNS(WSSE_NAMESPACE, "wsse:SecurityTokenReference");
        Element x509IssuerSerial = document.createElementNS(DS_NAMESPACE, "ds:X509IssuerSerial");

        // Add Issuer Name and Serial Number
        Element x509IssuerName = document.createElementNS(DS_NAMESPACE, "ds:X509IssuerName");
        x509IssuerName.setTextContent("CN=YourIssuerName, O=YourOrg, C=YourCountry");
        Element x509SerialNumber = document.createElementNS(DS_NAMESPACE, "ds:X509SerialNumber");
        x509SerialNumber.setTextContent("123456789");

        // Build the Structure
        x509IssuerSerial.appendChild(x509IssuerName);
        x509IssuerSerial.appendChild(x509SerialNumber);
        securityTokenReference.appendChild(x509IssuerSerial);
        newKeyInfo.appendChild(securityTokenReference);

        // Add New KeyInfo to Security Header
        security.appendChild(newKeyInfo);

        // Save and Print SOAP Message
        soapMessage.saveChanges();
        soapMessage.writeTo(System.out);
    }
}
