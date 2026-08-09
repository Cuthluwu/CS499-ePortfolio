import com.example.project.data.LegacyDataNormalizer;
import com.example.project.security.PasswordHasher;
import com.example.project.validation.InputValidator;
import java.util.Base64;

/** Standalone checks for the pure Java security, validation, and migration helpers. */
public final class StandaloneSecurityValidationTest {
    private static int checks;

    private StandaloneSecurityValidationTest() {
    }

    public static void main(String[] args) {
        char[] password = "correct horse battery staple".toCharArray();
        PasswordHasher.HashResult first = PasswordHasher.hash(password);
        PasswordHasher.HashResult second = PasswordHasher.hash(password);

        check(!first.getHash().equals(new String(password)), "password is not stored as plaintext");
        check(Base64.getDecoder().decode(first.getSalt()).length == PasswordHasher.SALT_BYTES,
                "salt has the configured 16-byte length");
        check(!first.getSalt().equals(second.getSalt()), "two accounts receive different salts");
        check(!first.getHash().equals(second.getHash()),
                "same password with different salt produces a different hash");
        check(PasswordHasher.ALGORITHM.equals(first.getAlgorithm()), "algorithm metadata is retained");
        check(first.getIterations() == PasswordHasher.ITERATIONS, "work-factor metadata is retained");
        check(PasswordHasher.verify(password, first.getSalt(), first.getHash(),
                first.getAlgorithm(), first.getIterations()), "correct password verifies");
        check(!PasswordHasher.verify("incorrect".toCharArray(), first.getSalt(), first.getHash(),
                first.getAlgorithm(), first.getIterations()), "incorrect password is rejected");
        check(!PasswordHasher.verify(password, "not-base64", first.getHash(),
                first.getAlgorithm(), first.getIterations()), "malformed salt is rejected safely");
        check(!PasswordHasher.verify(password, first.getSalt(), first.getHash(),
                "UnsupportedAlgorithm", first.getIterations()), "unsupported algorithm metadata is rejected");
        check(!PasswordHasher.verify(password, first.getSalt(), first.getHash(),
                first.getAlgorithm(), 0), "nonpositive work factor is rejected");
        check(!PasswordHasher.verify(password, first.getSalt(), first.getHash(),
                first.getAlgorithm(), PasswordHasher.MAX_ACCEPTED_ITERATIONS + 1),
                "excessive work factor is rejected before derivation");
        check(!PasswordHasher.verify(password, first.getSalt(), "not-base64",
                first.getAlgorithm(), first.getIterations()), "malformed hash is rejected safely");
        check(!PasswordHasher.verify(password,
                Base64.getEncoder().encodeToString(new byte[8]), first.getHash(),
                first.getAlgorithm(), first.getIterations()), "incorrect salt length is rejected");
        check(!PasswordHasher.verify(password, first.getSalt(),
                Base64.getEncoder().encodeToString(new byte[16]),
                first.getAlgorithm(), first.getIterations()), "incorrect hash length is rejected");

        char[] unavailableFirst = PasswordHasher.generateUnavailableCredential();
        char[] unavailableSecond = PasswordHasher.generateUnavailableCredential();
        check(unavailableFirst.length >= 40, "unavailable credential has at least 256 bits of entropy");
        check(!java.util.Arrays.equals(unavailableFirst, unavailableSecond),
                "unavailable credentials are independently randomized");
        java.util.Arrays.fill(unavailableFirst, '\0');
        java.util.Arrays.fill(unavailableSecond, '\0');

        check("Madison.Parker".equals(InputValidator.username("  Madison.Parker  ")),
                "username is trimmed");
        check("madison_parker-26".equals(InputValidator.username("madison_parker-26")),
                "documented username characters are accepted");
        check("weekly check".equals(InputValidator.note("  weekly check  ")), "note is trimmed");
        check("2026-08-01".equals(InputValidator.isoDate("2026-08-01")), "ISO date is accepted");
        check(InputValidator.weight(138.5) == 138.5, "valid weight is accepted");
        check(Double.valueOf(138.5).equals(LegacyDataNormalizer.parseWeight(" 138.5 ")),
                "legacy numeric weight is normalized");
        check("2026-06-22".equals(LegacyDataNormalizer.normalizeDate("June 22, 2026")),
                "legacy long date is normalized");
        check("2026-06-22".equals(LegacyDataNormalizer.normalizeDate("6/22/2026")),
                "legacy slash date is normalized");
        check(LegacyDataNormalizer.parseWeight("not-a-number") == null,
                "invalid legacy weight is not silently converted");
        check(LegacyDataNormalizer.normalizeDate("February 30, 2026") == null,
                "invalid legacy date is not silently converted");

        expectFailure(() -> PasswordHasher.hash(new char[0]), "empty password cannot be hashed");
        expectFailure(() -> InputValidator.username("x"), "short username is rejected");
        expectFailure(() -> InputValidator.username("Madison Parker"),
                "username with unsupported spaces is rejected");
        expectFailure(() -> InputValidator.password("short".toCharArray()), "short password is rejected");
        expectFailure(() -> InputValidator.isoDate("August 1, 2026"), "non-ISO new date is rejected");
        expectFailure(() -> InputValidator.isoDate("2026-02-30"), "impossible ISO date is rejected");
        expectFailure(() -> InputValidator.weight(0), "zero weight is rejected");
        expectFailure(() -> InputValidator.weight(Double.NaN), "non-finite weight is rejected");
        expectFailure(() -> InputValidator.note("x".repeat(251)), "overlong note is rejected");

        System.out.println("PASS: " + checks + " standalone Java checks completed successfully.");
    }

    private static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
        checks++;
        System.out.println("PASS: " + description);
    }

    private static void expectFailure(Runnable action, String description) {
        try {
            action.run();
            throw new AssertionError(description);
        } catch (IllegalArgumentException expected) {
            check(true, description);
        }
    }
}
