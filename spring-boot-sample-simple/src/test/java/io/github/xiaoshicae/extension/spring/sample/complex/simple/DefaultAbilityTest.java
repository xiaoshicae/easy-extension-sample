package io.github.xiaoshicae.extension.spring.sample.complex.simple;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultAbilityTest {
    DefaultAbility defaultAbility = new DefaultAbility();

    @Test
    void doSomething1() {
        String s = defaultAbility.doSomething1();
        assertEquals("default doSomething1", s);
    }

    @Test
    void doSomething2() {
        String s = defaultAbility.doSomething2();
        assertEquals("default doSomething2", s);
    }

    @Test
    void doSomething3() {
        String s = defaultAbility.doSomething3();
        assertEquals("default doSomething3", s);
    }
}
