package io.github.xiaoshicae.extension.spring.sample.complex.simple;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ControllerTest {

    @InjectMocks
    private Controller controller;

    @Mock
    private Ext1 ext1;

    @Mock
    private Ext2 ext2;

    @Spy
    private List<Ext3> ext3List = new ArrayList<>();

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void process() throws Exception {
        Ext3 ext3_1 = mock(Ext3.class);
        Ext3 ext3_2 = mock(Ext3.class);
        when(ext3_1.doSomething3()).thenReturn("do 3-1");
        when(ext3_2.doSomething3()).thenReturn("do 3-2");
        ext3List.add(ext3_1);
        ext3List.add(ext3_2);

        when(ext1.doSomething1()).thenReturn("do 1");
        when(ext2.doSomething2()).thenReturn("do 2");

        String res = controller.process();
        assertEquals("res: ext1 = do 1, ext2 = do 2, ext3List = [do 3-1, do 3-2]", res);
    }
}
