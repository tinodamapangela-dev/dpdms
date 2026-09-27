package zw.ac.uz.dpdms.mining.security;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class SharedKeys {
    public static final RSAPublicKey PUBLIC_KEY = load();
    private static RSAPublicKey load() {
        try {
            String stripped = DevPublicKey.PEM.replaceAll("-----[A-Z ]+-----", "").replaceAll("\\s", "");
            byte[] der = Base64.getDecoder().decode(stripped);
            return (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(der));
        } catch (Exception e) { throw new IllegalStateException("Failed to parse public.pem", e); }
    }
}