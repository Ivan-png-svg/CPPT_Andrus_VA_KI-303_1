package ua.lpnu.kzp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Reads the Maven-filtered project version from the packaged classpath. */
public final class VersionInfo {
    private VersionInfo() { }

    /** Returns the Maven project version, never a hardcoded Main constant. */
    public static String version() {
        Properties properties = new Properties();
        try (InputStream stream = VersionInfo.class.getResourceAsStream("/version.properties")) {
            if (stream == null) throw new IllegalStateException("version.properties missing");
            properties.load(stream);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read version.properties", e);
        }
        return properties.getProperty("version", "unknown");
    }
}
