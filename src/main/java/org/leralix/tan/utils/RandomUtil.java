package org.leralix.tan.utils;

import java.security.SecureRandom;
import java.util.Random;

public class RandomUtil {
    private RandomUtil() {

    }

    private static final SecureRandom random = new SecureRandom();

    static {
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
    }

    public static Random getRandom() {
        return random;
    }
}
