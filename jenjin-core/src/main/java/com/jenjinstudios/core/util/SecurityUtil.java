package com.jenjinstudios.core.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class contains security utility methods.
 *
 * @author Caleb Brinkman
 */
public class SecurityUtil
{
    private static final Logger LOGGER = Logger.getLogger(SecurityUtil.class.getName());
    public static final int KEYSIZE = 512;

    /**
     * Generate an RSA-512 Public-Private Key Pair.
	 *
	 * @return The generated {@code KeyPair}, or null if the KeyPair could not be created.
	 */
	public static KeyPair generateRSAKeyPair() {
		KeyPair keyPair = null;
		try
		{
			KeyPairGenerator keyPairGenerator;
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(KEYSIZE);
            keyPair = keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to create RSA key pair!", e);
		}
		return keyPair;
	}
}
