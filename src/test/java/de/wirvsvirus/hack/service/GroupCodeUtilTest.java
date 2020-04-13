package de.wirvsvirus.hack.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GroupCodeUtilTest {

    @Test
    void newCode() {
        final String code = GroupCodeUtil.generateGroupCode();
        assertTrue(Integer.parseInt(code) >= 100000);
    }
}