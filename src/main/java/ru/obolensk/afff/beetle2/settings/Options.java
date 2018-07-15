package ru.obolensk.afff.beetle2.settings;

import java.nio.file.Paths;

import javax.annotation.Nonnull;

import lombok.Getter;
import org.apache.log4j.Level;

/**
 * Created by Afff on 20.04.2017.
 * WARNING! Currently null value isn't supported to use in config.
 */
public enum Options {

    LOG_TO_CONSOLE(true),
    LOG_LEVEL(Level.INFO),
    SERVER_PORT(80),
    SO_TIMEOUT(600000),
    AWAIT_CONNECTION_SHUTDOWN_TIMEOUT(1000),
    SSL_KEYSTORE(Paths.get("keystore.pfx")),
    SSL_KEYSTORE_PASS(""),
    ROOT_DIR(Paths.get("/beetle")),
    WWW_DIR(Paths.get("www")),
    TEMP_DIR(Paths.get("temp")),
    SERVLETS_ENABLED(false),
    SERVLET_DIR(Paths.get("srv")),
    SERVLET_REFRESH_FILES_SERVICE_ENABLED(false),
    SERVLET_REFRESH_FILES_SERVICE_INTERVAL(60000),
    WELCOME_FILE_NAME("index.html"),
    SERVER_PARALLELISM_LEVEL(10),
    REQUEST_MAX_LINE_LENGTH(8192)
    ;

    Options(@Nonnull final Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Getter
    @Nonnull
    private final Object defaultValue;
}
