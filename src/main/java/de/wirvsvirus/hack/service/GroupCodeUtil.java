package de.wirvsvirus.hack.service;

import java.util.Random;

public class GroupCodeUtil {

    /**
     * must not start with 0
     */
    public static String generateGroupCode() {
        final int code = new Random().nextInt(1000000 - 100000) + 100000;
        return String.valueOf(code);
    }

}
