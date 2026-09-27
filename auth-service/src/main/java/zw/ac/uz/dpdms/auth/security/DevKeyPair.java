package zw.ac.uz.dpdms.auth.security;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public final class DevKeyPair {

    private static final PrivateKey PRIVATE_KEY = load();

    private static PrivateKey load() {
        try {
            Path p = Paths.get("keys", "private.pem").toAbsolutePath();
            if (!Files.exists(p)) {
                throw new IllegalStateException("private.pem not found at " + p +
                    ". Set the auth-service run configuration's working directory to the DPDMS root.");
            }
            String pem = Files.readString(p);
            String stripped = pem.replaceAll("-----[A-Z ]+-----", "").replaceAll("\\s", "");
            byte[] der = Base64.getDecoder().decode(stripped);
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(der));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load private key", e);
        }
    }

    public static PrivateKey privateKey() { return PRIVATE_KEY; }
}