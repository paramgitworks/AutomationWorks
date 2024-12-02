import org.apache.xml.security.Init;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.wss4j.common.crypto.Merlin;
import org.apache.xml.security.signature.XMLSignatureException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.security.PrivateKey;
import java.security.KeyStore;
import java.io.FileInputStream;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class WsSecSignatureWithXMLSignatureExample {

    public static void main(String[] args) throws Exception {
        // Initialize XML Security library for canonicalization and signing
        Init.init();

        // Load the XML document to sign
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);  // Important for WS-Security
        Document document = factory.newDocumentBuilder().parse("input.xml");

        // Load the keystore to retrieve the private key
        String keystoreFilePath = "path/to/your/keystore.jks";
        String keystorePassword = "keystorePassword";
        String keyAlias = "yourKeyAlias";
        String keyPassword = "yourKeyPassword";

        // Load the keystore
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream keystoreStream = new FileInputStream(keystoreFilePath)) {
            keystore.load(keystoreStream, keystorePassword.toCharArray());
        }

        // Check if the keystore contains the alias
        if (!keystore.containsAlias(keyAlias)) {
            throw new Exception("Keystore does not contain alias: " + keyAlias);
        }

        // Retrieve the private key
        PrivateKey privateKey = (PrivateKey) keystore.getKey(keyAlias, keyPassword.toCharArray());
        if (privateKey == null) {
            throw new Exception("Private key not found for alias: " + keyAlias);
        }

        // Retrieve the certificate for the public key (optional for KeyInfo)
        X509Certificate certificate = (X509Certificate) keystore.getCertificate(keyAlias);

        // Apply canonicalization on the entire document (if needed)
        Canonicalizer canonicalizer = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        byte[] canonicalXml = canonicalizer.canonicalize(document);

        // Create the XMLSignature object to sign the document
        XMLSignature signature = new XMLSignature(document, null, XMLSignature.ALGO_ID_SIGNATURE_RSA);

        // Create a KeyInfo element and add the private key (for verification later)
        KeyInfo keyInfo = new KeyInfo(document);
        keyInfo.addKeyName("yourKeyAlias"); // Add the alias or public key info if needed
        keyInfo.addCertificate(certificate); // Optional: Add certificate for public verification

        // Attach the KeyInfo to the signature
        signature.setKeyInfo(keyInfo);

        // Prepare the element to be signed (you can choose the part of the document you want to sign)
        Element elementToSign = document.getDocumentElement();  // Example: sign the root element

        // Perform the signing
        signature.sign(privateKey);

        // Optionally, you can apply the signature in the header or specific part of the XML document
        document.getDocumentElement().appendChild(signature.getElement());

        // Convert the signed document to a string
        String signedXml = org.apache.wss4j.dom.util.WSSecurityUtil.toString(document);

        // Print the signed XML
        System.out.println("Signed XML:\n" + signedXml);

        // You can now use this signed XML in Rest Assured
        // RestAssured.given().body(signedXml).post("your-endpoint");
    }
}
