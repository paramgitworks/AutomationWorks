import java.io.File;
import java.io.FileInputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.keyinfo.X509Certificate;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.xml.security.Init;
import org.apache.xml.security.signature.XMLSignatureFactory;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.XMLUtils;
import javax.xml.parsers.DocumentBuilderFactory;

public class XMLSignatureExample {

    static {
        Init.init();  // Initialize XML Security library
    }

    public static void main(String[] args) throws Exception {
        // Load the XML document
        FileInputStream xmlFile = new FileInputStream(new File("input.xml"));
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(xmlFile);
        
        // Load the private key and certificate
        PrivateKey privateKey = loadPrivateKey("privateKey.pem"); // Load your private key
        X509Certificate cert = loadCertificate("certificate.pem"); // Load your X.509 certificate

        // Create the XML Signature factory
        XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");

        // Create the KeyInfo
        KeyInfo keyInfo = createKeyInfo(cert, signatureFactory);

        // Create the XML Signature
        XMLSignature signature = signatureFactory.newXMLSignature(
            signatureFactory.newSignedInfo(
                signatureFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (XMLStructure) null),
                signatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA256, null),
                Collections.singletonList(signatureFactory.newReference(
                    "#element-id", 
                    signatureFactory.newDigestMethod(DigestMethod.SHA256, null)
                ))
            ),
            keyInfo
        );

        // Sign the document
        DOMSignContext signContext = new DOMSignContext(privateKey, doc.getDocumentElement());
        signature.sign(signContext);

        // Output the signed document to a file
        File outputFile = new File("signed_output.xml");
        XMLUtils.outputDOM(doc, outputFile);

        System.out.println("Document signed successfully.");
    }

    private static KeyInfo createKeyInfo(X509Certificate cert, XMLSignatureFactory signatureFactory) throws Exception {
        // Create X509Data to hold the certificate
        X509Data x509Data = signatureFactory.newX509Data(Collections.singletonList(cert.getEncoded()));
        
        // Create and return KeyInfo with the certificate inside it
        return signatureFactory.newKeyInfo(Collections.singletonList(x509Data));
    }

    private static PrivateKey loadPrivateKey(String filePath) {
        // Implement loading of your private key (from a PEM, etc.)
        // For example, you can use libraries like BouncyCastle or KeyFactory
        return null; // Placeholder for actual private key loading logic
    }

    private static X509Certificate loadCertificate(String filePath) {
        // Implement loading your certificate from a file
        return null; // Placeholder for actual certificate loading logic
    }
}
