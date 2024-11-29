import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class SoapApiAutomation {
    public static void main(String[] args) {
        try {
            // Load the keystore
            FileInputStream fis = new FileInputStream("path/to/your/keystore.jks");
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(fis, "your_keystore_password".toCharArray());

            // Initialize KeyManagerFactory and TrustManagerFactory
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keystore, "your_keystore_password".toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keystore);

            // Set up RestAssured with SSL context
            RestAssured.requestSpecification = new RequestSpecBuilder()
                    .setBaseUri("https://api.example.com")
                    .setContentType("application/soap+xml")
                    .build();

            // Make the SOAP POST request
            Response response = RestAssured.given()
                    .body("<SOAP-ENV:Envelope>...</SOAP-ENV:Envelope>")
                    .post("/your_soap_endpoint");

            // Print the response
            System.out.println(response.asString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
