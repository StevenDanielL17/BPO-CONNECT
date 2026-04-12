package com.bpoconnect.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordUtil {

	private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
	private static final int ITERATIONS = 120000;
	private static final int KEY_LENGTH = 256;
	private static final int SALT_LENGTH = 16;
	private static final SecureRandom RANDOM = new SecureRandom();

	private PasswordUtil() {
	}

	public static String hashPassword(String password) {
		if (password == null) {
			return null;
		}

		byte[] salt = new byte[SALT_LENGTH];
		RANDOM.nextBytes(salt);
		byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
		return String.join("$",
				"pbkdf2",
				Integer.toString(ITERATIONS),
				Base64.getEncoder().encodeToString(salt),
				Base64.getEncoder().encodeToString(hash));
	}

	public static boolean verifyPassword(String password, String storedValue) {
		if (password == null || storedValue == null) {
			return false;
		}

		if (!storedValue.startsWith("pbkdf2$")) {
			return storedValue.equals(password);
		}

		String[] parts = storedValue.split("\\$");
		if (parts.length != 4) {
			return false;
		}

		int iterations = Integer.parseInt(parts[1]);
		byte[] salt = Base64.getDecoder().decode(parts[2]);
		byte[] expectedHash = Base64.getDecoder().decode(parts[3]);
		byte[] actualHash = pbkdf2(password.toCharArray(), salt, iterations, expectedHash.length * 8);
		return constantTimeEquals(expectedHash, actualHash);
	}

	private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) {
		try {
			PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
			SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
			return factory.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
			throw new IllegalStateException("Unable to hash password", exception);
		}
	}

	private static boolean constantTimeEquals(byte[] expected, byte[] actual) {
		if (expected.length != actual.length) {
			return false;
		}

		int result = 0;
		for (int index = 0; index < expected.length; index++) {
			result |= expected[index] ^ actual[index];
		}
		return result == 0;
	}
}
