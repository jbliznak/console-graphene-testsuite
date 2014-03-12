package org.jboss.as.console.testsuite.tests.util;

import org.jboss.as.console.testsuite.util.PropUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * Created by jcechace on 21/02/14.
 */
public class ConfigUtils {

    private static final Properties config = new Properties();
    static {
        // Load default configuration
        try {
            InputStream defaultConfig = PropUtils.class.getResourceAsStream("/suite.properties");
            try {
                config.load(defaultConfig);
            } finally {
                defaultConfig.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to load suite configuration");
        }

        // Override by custom configuration
        String customConfigLocation = System.getProperty("suite.config.location");
        if (customConfigLocation != null) {
            try {
                InputStream customConfig = new FileInputStream(customConfigLocation);
                try {
                    config.load(customConfig);
                } finally {
                    customConfig.close();
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to load custom suite configuration");
            }
        }

        // Override by properties from command line
        config.putAll(System.getProperties());

    }


    public static String get(String key) {
        return config.getProperty(key);
    }

    public static String get(String key, String defval) {
        return config.getProperty(key, defval);
    }

    public static boolean isEAP() {
        return get("suite.mode", "eap").toLowerCase().equals("eap");
    }

    public static String getProtocol() {
        return get("suite.url.protocol", "http");
    }

    public static String getHost() {
        return get("suite.url.host", "localhost");
    }

    public static int getPort() {
        return Integer.parseInt(get("suite.url.port", "9990"));
    }

    public static String getContext() {
        return get("suite.url.context.path", "/console/App.html");
    }

    public static URL getUrl() {
        try {
            URL url = new URL(getProtocol(), getHost(), getPort(), getContext());
            return url;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid url", e);
        }
    }
}
