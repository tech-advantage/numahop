package fr.progilone.pgcn.service.util;

import static org.apache.commons.text.CharacterPredicates.*;

import org.apache.commons.text.RandomStringGenerator;

/**
 * Utility class for generating random Strings.
 */
public final class RandomUtil {

    private static final int DEF_PWD_COUNT = 8;
    private static final int DEF_KEY_COUNT = 20;

    private RandomUtil() {
    }

    /**
     * Generates a password.
     *
     * @return the generated password
     */
    public static String generatePassword() {
        return new RandomStringGenerator.Builder().withinRange('0', 'z').filteredBy(LETTERS, DIGITS).build().generate(DEF_PWD_COUNT);
    }

    /**
     * Generates an activation key.
     *
     * @return the generated activation key
     */
    public static String generateActivationKey() {
        return new RandomStringGenerator.Builder().withinRange('0', 'z').filteredBy(LETTERS, DIGITS).build().generate(DEF_KEY_COUNT);
    }
}
