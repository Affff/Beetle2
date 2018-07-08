package ru.obolensk.afff.beetle2.util;

/**
 * Created by Afff on 11.04.2017.
 */
public class Version {

    private static final String SERVER_NAME = "Beetle2 HTTP server";
    private static final String SERVER_VERSION = "0.0.1"; //FIXME read this from gradle build
    private static final String SERVER_NAME_AND_VERSION = SERVER_NAME + " v" + SERVER_VERSION;

    public static String nameAndVersion() {
        return SERVER_NAME_AND_VERSION;
    }
}
