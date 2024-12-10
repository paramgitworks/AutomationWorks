SOAPFactory soapFactory = SOAPFactory.newInstance();
SOAPElement keyInfo = soapFactory.createElement("KeyInfo", "ds", "http://www.w3.org/2000/09/xmldsig#");
SOAPElement securityTokenReference = soapFactory.createElement("SecurityTokenReference", "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");

SOAPElement issuerSerial = soapFactory.createElement("X509IssuerSerial", "ds", "http://www.w3.org/2000/09/xmldsig#");
SOAPElement issuerNameElement = soapFactory.createElement("X509IssuerName", "ds", "http://www.w3.org/2000/09/xmldsig#");
issuerNameElement.addTextNode(issuerName);

SOAPElement serialNumberElement = soapFactory.createElement("X509SerialNumber", "ds", "http://www.w3.org/2000/09/xmldsig#");
serialNumberElement.addTextNode(serialNumber);

issuerSerial.addChildElement(issuerNameElement);
issuerSerial.addChildElement(serialNumberElement);

securityTokenReference.addChildElement(issuerSerial);
keyInfo.addChildElement(securityTokenReference);

// Add KeyInfo to the SOAP Security Header
