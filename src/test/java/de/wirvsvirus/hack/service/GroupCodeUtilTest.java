package de.wirvsvirus.hack.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GroupCodeUtilTest {

    @Test
    void newCode() {
        final String code = GroupCodeUtil.generateGroupCode();
        assertTrue(Integer.parseInt(code) >= 100000);
    }
}