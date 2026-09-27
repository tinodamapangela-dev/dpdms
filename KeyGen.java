import java.nio.file.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class KeyGen {
    public static void main(String[] args) throws Exception {
        Path keysDir = Path.of("keys");
        Files.createDirectories(keysDir);

        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair kp = gen.generateKeyPair();

        String priv = "-----BEGIN PRIVATE KEY-----\n" +
            Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(kp.getPrivate().getEncoded()) +
            "\n-----END PRIVATE KEY-----\n";
        String pub = "-----BEGIN PUBLIC KEY-----\n" +
            Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(kp.getPublic().getEncoded()) +
            "\n-----END PUBLIC KEY-----\n";

        Files.writeString(keysDir.resolve("private.pem"), priv);
        Files.writeString(keysDir.resolve("public.pem"), pub);
        System.out.println("Wrote keys/private.pem and keys/public.pem");
    }
}