package io.github.xiaoshicae.extension.spring.sample.complex.simple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessATest {
    private BusinessA businessA;

    @BeforeEach
    void setUp() {
        businessA = new BusinessA();
    }

    @Test
    void code() {
        assertEquals("x.business.a", businessA.code());
    }

    @Test
    void match() {
        Boolean match = businessA.match(null);
        assertFalse(match);

        match = businessA.match(new MyParam("x"));
        assertFalse(match);

        match = businessA.match(new MyParam("a"));
        assertTrue(match);
    }

    @Test
    void priority() {
        assertEquals(100, businessA.priority());
    }

    @Test
    void usedAbilities() {
        assertEquals(0, businessA.usedAbilities().size());
    }

    @Test
    void doSomething1() {
        assertEquals("businessA doSomething1", businessA.doSomething1());
    }

    @Test
    void doSomething2() {
        assertEquals("businessA doSomething2", businessA.doSomething2());
    }
}