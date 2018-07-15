package ru.obolensk.afff.beetle2.util;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Afff on 11.04.2017.
 */
public class Version {

    private static final String SERVER_NAME;
    private static final String SERVER_VERSION;
    private static final String SERVER_NAME_AND_VERSION;

    static {
        Properties props = new Properties();
        try {
            props.load(Version.class.getResourceAsStream("/version.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SERVER_NAME = props.getProperty("application.name");
        SERVER_VERSION = props.getProperty("application.version");
        SERVER_NAME_AND_VERSION = SERVER_NAME + " v" + SERVER_VERSION;
    }

    public static String nameAndVersion() {
        return SERVER_NAME_AND_VERSION;
    }
}
