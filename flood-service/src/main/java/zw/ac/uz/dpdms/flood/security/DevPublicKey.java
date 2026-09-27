package zw.ac.uz.dpdms.flood.security;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class DevPublicKey {
    public static final String PEM = load();
    private static String load() {
        try {
            Path p = Paths.get("keys", "public.pem").toAbsolutePath();
            if (!Files.exists(p)) {
                throw new IllegalStateException("public.pem not found at " + p +
                    ". Set the working directory to the DPDMS root.");
            }
            return Files.readString(p);
        } catch (Exception e) { throw new IllegalStateException(e); }
    }
}