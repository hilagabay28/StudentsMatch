package com.example.studentsmatch;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hashes passwords with PBKDF2 (HMAC-SHA256) and a random salt, so the database
 * never stores a password in plain text.
 *
 * Stored format: pbkdf2$<iterations>$<salt base64>$<hash base64>
 */
public final class PasswordHasher {

    private static final String PREFIX = "pbkdf2";
    private static final int ITERATIONS = 120_000;
    private static final int SALT_BYTES = 16;
    private static final int KEY_BITS = 256;

    private PasswordHasher() {}

    public static String hash(String password) {
        byte[] salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        byte[] key = derive(password, salt, ITERATIONS);
        return PREFIX + "$" + ITERATIONS + "$" + encode(salt) + "$" + encode(key);
    }

    /** True if {@code password} matches the stored value. */
    public static boolean verify(String password, String stored) {
        if (password == null || stored == null) return false;
        if (!isHashed(stored)) {
            // Accounts created before hashing was added still hold the plain password.
            return MessageDigest.isEqual(password.getBytes(), stored.getBytes());
        }
        String[] parts = stored.split("\\$");
        if (parts.length != 4) return false;
        int iterations = Integer.parseInt(parts[1]);
        byte[] salt = Base64.decode(parts[2], Base64.NO_WRAP);
        byte[] expected = Base64.decode(parts[3], Base64.NO_WRAP);
        byte[] actual = derive(password, salt, iterations);
        return MessageDigest.isEqual(expected, actual);
    }

    /** False for old accounts whose password is still stored in plain text. */
    public static boolean isHashed(String stored) {
        return stored != null && stored.startsWith(PREFIX + "$");
    }

    private static byte[] derive(String password, byte[] salt, int iterations) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("Password hashing is not available", e);
        }
    }

    private static String encode(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }
}
