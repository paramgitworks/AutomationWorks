DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
factory.setNamespaceAware(true); 
DocumentBuilder db = factory.newDocumentBuilder();
InputSource is = new InputSource(
    new StringReader(FileUtils.readFileToString(new File(XmlFilePath), StandardCharsets.UTF_8))
);
Document doc = db.parse(is);

// Load keystore
KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
try (FileInputStream keystoreStream = new FileInputStream(jksFilePath)) {
    keystore.load(keystoreStream, keystorePassword.toCharArray());
}
String keyAlias = keystore.aliases().nextElement();
if (!keystore.containsAlias(keyAlias)) {
    throw new Exception("Keystore does not contain alias: " + keyAlias);
}
PrivateKey privateKey = (PrivateKey) keystore.getKey(keyAlias, keystorePassword.toCharArray());
if (privateKey == null) {
    throw new Exception("Private key not found for alias: " + keyAlias);
}
X509Certificate xc = (X509Certificate) keystore.getCertificate(keyAlias);

// Set the ID attribute for the SOAP Body
Element sb = (Element) doc.getElementsByTagName("soapenv:Body").item(0);
sb.setIdAttributeNS(
    "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", 
    "Id", true
);
String bid = sb.getAttributeNS(
    "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", 
    "Id"
);

// Create the SOAP Header and Security elements
Element ee = doc.getDocumentElement();
Element be = (Element) ee.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Body").item(0);
Element he = doc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Header");
Element se = doc.createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse:Security");
se.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");

he.appendChild(se);
ee.insertBefore(he, be);

// Create the XML Signature
XMLSignature xs = new XMLSignature(doc, null, XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);
xs.getElement().setAttribute("Id", "SIG-9E60F889A6A6AC8B01173312607567514");

// Add Transforms
Transforms trans = new Transforms(doc);
trans.addTransform(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
Element tl = trans.item(0).getElement();
Element ins = doc.createElementNS("http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces");
ins.setAttribute("PrefixList", "tran");
tl.appendChild(ins);

xs.addDocument("#" + bid, trans, Constants.ALGO_ID_DIGEST_SHA1);

// Add KeyInfo
KeyInfo keyInfo = new KeyInfo(doc);
Element str = doc.createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsse:SecurityTokenReference");
str.setAttributeNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu:Id", "STR-9E60F889A6A6AC8B01173312607566723");

X509Data xd = new X509Data(doc);
XMLX509IssuerSerial xis = new XMLX509IssuerSerial(doc, xc);
xd.add(xis);
str.appendChild(xd.getElement());

keyInfo.getElement().setAttribute("Id", "KI-9E60F889A6A6AC8B01173312607560738");
keyInfo.getElement().appendChild(str);

// Append KeyInfo and Sign
xs.getElement().appendChild(keyInfo.getElement());
xs.sign(privateKey);
se.appendChild(xs.getElement());

// Transform to String
StringWriter sw = new StringWriter();
TransformerFactory transformerFactory = TransformerFactory.newInstance();
Transformer tr = transformerFactory.newTransformer();
tr.transform(new DOMSource(doc), new StreamResult(sw));

System.out.println("Signed XML: " + sw.toString());
