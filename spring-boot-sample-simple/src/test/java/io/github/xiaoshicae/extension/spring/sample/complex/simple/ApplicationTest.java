package io.github.xiaoshicae.extension.spring.sample.complex.simple;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@SpringBootTest
@AutoConfigureMockMvc
class ApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void mock() throws Exception {
        //mockMvc.perform执行一个请求
        mockMvc.perform(MockMvcRequestBuilders
                        //构造请求
                        .get("/api/process")
                        //设置返回值类型
                        .accept(MediaType.APPLICATION_JSON)
                        //添加请求参数
                        .param("name", "a"))
                //添加执行完成后的断言
                .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("res: ext1 = businessA doSomething1, ext2 = businessA doSomething2, ext3List = [default doSomething3]"))
                //添加一个结果处理器，此处打印整个响应结果信息
                .andDo(MockMvcResultHandlers.print());
    }
}