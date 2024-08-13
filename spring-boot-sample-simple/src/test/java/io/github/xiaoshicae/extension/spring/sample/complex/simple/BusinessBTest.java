package io.github.xiaoshicae.extension.spring.sample.complex.simple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessBTest {
    private BusinessB businessB;

    @BeforeEach
    void setUp() {
        businessB = new BusinessB();
    }

    @Test
    void code() {
        assertEquals("x.business.b", businessB.code());
    }

    @Test
    void match() {
        Boolean match = businessB.match(null);
        assertFalse(match);

        match = businessB.match(new MyParam("x"));
        assertFalse(match);

        match = businessB.match(new MyParam("b"));
        assertTrue(match);
    }

    @Test
    void priority() {
        assertEquals(100, businessB.priority());
    }

    @Test
    void usedAbilities() {
        assertEquals(0, businessB.usedAbilities().size());
    }

    @Test
    void doSomething1() {
        assertEquals("businessB doSomething1", businessB.doSomething1());
    }

    @Test
    void doSomething3() {
        assertEquals("businessB doSomething3", businessB.doSomething3());
    }
}