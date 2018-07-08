package ru.obolensk.afff.beetle2.core;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.Nonnull;

import lombok.Getter;
import ru.obolensk.afff.beetle2.log.Logger;
import ru.obolensk.afff.beetle2.settings.Config;
import ru.obolensk.afff.beetle2.settings.Options;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.exists;
import static ru.obolensk.afff.beetle2.settings.Options.ROOT_DIR;
import static ru.obolensk.afff.beetle2.settings.Options.SERVLET_DIR;
import static ru.obolensk.afff.beetle2.settings.Options.TEMP_DIR;
import static ru.obolensk.afff.beetle2.settings.Options.WELCOME_FILE_NAME;
import static ru.obolensk.afff.beetle2.settings.Options.WWW_DIR;

/**
 * Created by Afff on 10.04.2017.
 */
public class Storage {

    private static final Logger logger = new Logger(Storage.class);

    private static final String URI_SEPARATOR = "/";

    public static final Path ROOT = Paths.get(URI_SEPARATOR);

    @Getter
    private final Config config;

    Storage(@Nonnull final Config config) throws IOException {
        this.config = config;
        checkAndCreateDirIfNeeded(getWwwDir());
        checkAndCreateDirIfNeeded(getServletDir());
        checkAndCreateDirIfNeeded(getTempDir());
    }

    private void checkAndCreateDirIfNeeded(@Nonnull final Path dir) throws IOException {
        if (!exists(dir)) {
            createDirectories(dir);
        }
    }

    @Nonnull
    private Path getRootDir() {
        return config.get(ROOT_DIR);
    }

    @Nonnull
    public Path getWwwDir() {
        return getPathFor(WWW_DIR);
    }

    @Nonnull
    public Path getServletDir() {
        return getPathFor(SERVLET_DIR);
    }

	@Nonnull
    private Path getTempDir() {
		return getPathFor(TEMP_DIR);
	}

    private Path getPathFor(@Nonnull final Options option) {
        return getRootDir().resolve((Path) config.get(option));
    }

    @Nonnull
    private String getWelcomeFileName() {
        return config.get(WELCOME_FILE_NAME);
    }

    void reloadServlets() throws IOException {
        //TODO add servlet support servletContainer.update();
    }
}
