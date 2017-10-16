package com.zxz.filter;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Security;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyBogusSslContextFactory {

	private static final Logger logger = LoggerFactory.getLogger(MyBogusSslContextFactory.class);

	private static String serverKeys = "serverKeys.jks";
	private static String serverKeysPassword = "123456";

	private static String clientKeys = "clientKeys.jks";
	private static String clientKeysPassword = "123456";

	/**
	 * Protocol to use.
	 */
	private static final String PROTOCOL = "TLS";
	private static final String KEY_MANAGER_FACTORY_ALGORITHM;	

	static {
		String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
		if (algorithm == null) {
			algorithm = KeyManagerFactory.getDefaultAlgorithm();
		}

		KEY_MANAGER_FACTORY_ALGORITHM = algorithm;
	}

	private static SSLContext serverInstance = null;
	private static SSLContext clientInstance = null;

	/**
	 * Get SSLContext singleton.
	 * 
	 * @return SSLContext
	 * @throws java.security.GeneralSecurityException
	 * 
	 */
	public static SSLContext getInstance(boolean server) throws GeneralSecurityException, IOException {
		SSLContext retInstance = null;
		if (server) {
			synchronized (MyBogusSslContextFactory.class) {
				if (serverInstance == null) {
					try {
						serverInstance = createBougusServerSslContext();
					} catch (Exception ioe) {
						ioe.printStackTrace();
						throw new GeneralSecurityException("Can't create Server SSLContext:" + ioe);
					}
				}
			}
			retInstance = serverInstance;
		} else {
			synchronized (MyBogusSslContextFactory.class) {
				if (clientInstance == null) {
					clientInstance = createBougusClientSslContext();
				}
			}
			retInstance = clientInstance;
		}
		return retInstance;
	}

	private static SSLContext createBougusServerSslContext() throws GeneralSecurityException, IOException {

		// Initialize the SSLContext to work with our key managers.
		SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
		sslContext.init(getKeyManagers(serverKeys, serverKeysPassword), MyBogusTrustManagerFactory.X509_MANAGERS, null);

		return sslContext;
	}

	private static SSLContext createBougusClientSslContext() throws GeneralSecurityException, IOException {
		SSLContext context = SSLContext.getInstance(PROTOCOL);
		context.init(getKeyManagers(clientKeys, clientKeysPassword), MyBogusTrustManagerFactory.X509_MANAGERS, null);
		return context;
	}

	private static KeyManager[] getKeyManagers(String keysfile, String password) throws GeneralSecurityException,
			IOException {

		KeyStore ks = KeyStore.getInstance("JKS");
		InputStream in = MyBogusSslContextFactory.class.getResourceAsStream(keysfile);
		ks.load(in, password.toCharArray());
		in.close();

		// Set up key manager factory to use our key store
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KEY_MANAGER_FACTORY_ALGORITHM);
		kmf.init(ks, password.toCharArray());

		return kmf.getKeyManagers();
	}

}
