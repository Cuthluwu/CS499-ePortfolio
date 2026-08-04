package com.example.project.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class PasswordHasherTest {
    @Test
    public void uniqueSaltsProduceDifferentHashesAndBothVerify() {
        char[] password = "correct horse battery staple".toCharArray();
        PasswordHasher.HashResult first = PasswordHasher.hash(password);
        PasswordHasher.HashResult second = PasswordHasher.hash(password);

        assertNotEquals(first.getSalt(), second.getSalt());
        assertNotEquals(first.getHash(), second.getHash());
        assertTrue(PasswordHasher.verify(
                password,
                first.getSalt(),
                first.getHash(),
                first.getAlgorithm(),
                first.getIterations()));
        assertFalse(PasswordHasher.verify(
                "wrong password".toCharArray(),
                first.getSalt(),
                first.getHash(),
                first.getAlgorithm(),
                first.getIterations()));
    }

    @Test
    public void malformedOrUnsupportedVerifierIsRejectedWithoutCrashing() {
        char[] password = "correct horse battery staple".toCharArray();
        PasswordHasher.HashResult result = PasswordHasher.hash(password);

        assertFalse(PasswordHasher.verify(password, "not-base64", result.getHash(),
                result.getAlgorithm(), result.getIterations()));
        assertFalse(PasswordHasher.verify(password, result.getSalt(), result.getHash(),
                "UnsupportedAlgorithm", result.getIterations()));
        assertFalse(PasswordHasher.verify(password, result.getSalt(), result.getHash(),
                result.getAlgorithm(), 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyPasswordCannotBeHashed() {
        PasswordHasher.hash(new char[0]);
    }
}
