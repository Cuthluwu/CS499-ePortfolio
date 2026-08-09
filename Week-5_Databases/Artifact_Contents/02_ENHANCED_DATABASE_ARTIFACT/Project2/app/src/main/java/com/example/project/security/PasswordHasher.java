package com.example.project.security;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** PBKDF2 password hashing with a unique random salt for each account. */
public final class PasswordHasher {
    public static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    public static final int ITERATIONS = 600_000;
    public static final int MAX_ACCEPTED_ITERATIONS = 1_200_000;
    public static final int SALT_BYTES = 16;
    public static final int KEY_LENGTH_BITS = 256;
    public static final int UNAVAILABLE_CREDENTIAL_BYTES = 32;

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordHasher() {
    }

    public static HashResult hash(char[] password) {
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("Password is required.");
        }
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        byte[] derived = derive(password, salt, ITERATIONS, ALGORITHM);
        return new HashResult(
                Base64.getEncoder().encodeToString(derived),
                Base64.getEncoder().encodeToString(salt),
                ALGORITHM,
                ITERATIONS);
    }

    /**
     * Creates a high-entropy credential for migrated records that must not be
     * accessible through a predictable legacy password.
     */
    public static char[] generateUnavailableCredential() {
        byte[] randomBytes = new byte[UNAVAILABLE_CREDENTIAL_BYTES];
        RANDOM.nextBytes(randomBytes);
        try {
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(randomBytes)
                    .toCharArray();
        } finally {
            java.util.Arrays.fill(randomBytes, (byte) 0);
        }
    }

    public static boolean verify(
            char[] candidate,
            String encodedSalt,
            String encodedHash,
            String algorithm,
            int iterations) {
        if (candidate == null || encodedSalt == null || encodedHash == null) {
            return false;
        }
        if (!ALGORITHM.equals(algorithm)
                || iterations <= 0
                || iterations > MAX_ACCEPTED_ITERATIONS) {
            return false;
        }
        try {
            byte[] salt = Base64.getDecoder().decode(encodedSalt);
            byte[] expected = Base64.getDecoder().decode(encodedHash);
            if (salt.length != SALT_BYTES || expected.length != KEY_LENGTH_BITS / Byte.SIZE) {
                return false;
            }
            byte[] actual = derive(candidate, salt, iterations, algorithm);
            return MessageDigest.isEqual(expected, actual);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private static byte[] derive(
            char[] password,
            byte[] salt,
            int iterations,
            String algorithm) {
        PBEKeySpec specification = new PBEKeySpec(password, salt, iterations, KEY_LENGTH_BITS);
        try {
            return SecretKeyFactory.getInstance(algorithm)
                    .generateSecret(specification)
                    .getEncoded();
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Password hashing is unavailable.", exception);
        } finally {
            specification.clearPassword();
        }
    }

    public static final class HashResult {
        private final String hash;
        private final String salt;
        private final String algorithm;
        private final int iterations;

        public HashResult(String hash, String salt, String algorithm, int iterations) {
            this.hash = hash;
            this.salt = salt;
            this.algorithm = algorithm;
            this.iterations = iterations;
        }

        public String getHash() {
            return hash;
        }

        public String getSalt() {
            return salt;
        }

        public String getAlgorithm() {
            return algorithm;
        }

        public int getIterations() {
            return iterations;
        }
    }
}
