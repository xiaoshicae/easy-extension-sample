package io.github.xiaoshicae.extension.spring.sample.complex.simple;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MyParamTest {

    @Test
    void getName() {
        MyParam myParam = new MyParam("m");
        assertEquals("m", myParam.getName());
    }
}