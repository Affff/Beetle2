package ru.obolensk.afff.beetle2.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.Level;
import ru.obolensk.afff.beetle2.core.BeetleServer;
import ru.obolensk.afff.beetle2.settings.Options;
import ru.obolensk.afff.beetle2.settings.ServerConfig;
import ru.obolensk.afff.beetle2.util.Version;

/**
 * Created by Afff on 10.04.2017.
 */
class ConsoleClient {

    private static BeetleServer server;

    public static void main(@Nonnull String[] args) throws IOException, GeneralSecurityException {
        ServerConfig config = new ServerConfig();
        config.set(Options.SERVER_PORT, 4080);
        if (args.length != 0) {
            config.set(Options.ROOT_DIR, Paths.get(args[0]));
        }
        if (args.length > 1) {
            config.set(Options.SSL_KEYSTORE, Paths.get(args[1]));
        }
        if (args.length > 2) {
            config.set(Options.SSL_KEYSTORE_PASS, args[2]);
        }
        config.set(Options.LOG_LEVEL, Level.DEBUG);
        server = new BeetleServer(config);
        printMenu();
    }

    private static void printMenu() throws IOException {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
            printf("Welcome to %s!", Version.nameAndVersion());
            while (true) {
                printf("Menu:");
                printf("q) Shutdown server");
                printf("Enter the command:");
                final String cmd = reader.readLine();
                if ("q".equals(cmd)) {
                    server.close();
                    break;
                }
            }
        }
    }

    private static void printf(@Nonnull final String msg, @Nullable final Object... params) {
        if (params != null && params.length != 0) {
            System.out.printf(msg + "\r\n", params);
        } else {
            System.out.println(msg);
        }
    }
}
