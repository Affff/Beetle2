package ru.obolensk.afff.beetle2.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.annotation.Nonnull;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import ru.obolensk.afff.beetle2.settings.Config;

import static ru.obolensk.afff.beetle2.settings.Options.ROOT_DIR;
import static ru.obolensk.afff.beetle2.settings.Options.SERVER_PORT;
import static ru.obolensk.afff.beetle2.settings.Options.SO_TIMEOUT;
import static ru.obolensk.afff.beetle2.settings.Options.SSL_KEYSTORE;
import static ru.obolensk.afff.beetle2.settings.Options.SSL_KEYSTORE_PASS;

/**
 * Created by Afff on 05.09.2017.
 */
class SslSocketFactory {

	private static final String DEFAULT_KEYSTORE_FORMAT = "PKCS12";
	private static final String DEFAULT_SERTIFICATE_FORMAT = "SunX509";
	private static final String DEFAULT_SSL_PROTOCOL = "TLSv1.2";

	@Nonnull
	static SSLServerSocket createHttp2SslServerSocket(@Nonnull final Config config) throws IOException, GeneralSecurityException {
		final int port = config.get(SERVER_PORT);
		final SSLContext sc = createSslContext(config);
		final SSLServerSocketFactory ssf = sc.getServerSocketFactory();
		SSLServerSocket sslServerSocket = (SSLServerSocket) ssf.createServerSocket(port);
		SSLParameters params = sslServerSocket.getSSLParameters();
		params.setApplicationProtocols(new String[] { "h2" }); // support for HTTP2 protocol only
		sslServerSocket.setSSLParameters(params);
		sslServerSocket.setSoTimeout(config.get(SO_TIMEOUT));
		return sslServerSocket;
	}

	@Nonnull
	private static SSLContext createSslContext(@Nonnull final Config config) throws IOException, GeneralSecurityException {
		final Path root = config.get(ROOT_DIR);
		final Path keystore = root.resolve((Path) config.get(SSL_KEYSTORE));
		final String keystorePass = config.get(SSL_KEYSTORE_PASS);
		return initSslContext(keystore, keystorePass.toCharArray());
	}

	private static SSLContext initSslContext(final @Nonnull Path keystore, final char[] keystorePass) throws GeneralSecurityException, IOException {
		final KeyStore ks = KeyStore.getInstance(DEFAULT_KEYSTORE_FORMAT);
		ks.load(new FileInputStream(keystore.toFile()), keystorePass);

		final KeyManagerFactory kmf = KeyManagerFactory.getInstance(DEFAULT_SERTIFICATE_FORMAT);
		kmf.init(ks, keystorePass);

		final TrustManagerFactory tmf = TrustManagerFactory.getInstance(DEFAULT_SERTIFICATE_FORMAT);
		tmf.init(ks);

		final SSLContext sc = SSLContext.getInstance(DEFAULT_SSL_PROTOCOL);
		TrustManager[] trustManagers = tmf.getTrustManagers();
		sc.init(kmf.getKeyManagers(), trustManagers, null);
		return sc;
	}
}
