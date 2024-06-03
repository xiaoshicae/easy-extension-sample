package io.github.xiaoshicae.extension.spring.sample.complex.controller;

import io.github.xiaoshicae.extension.spring.sample.complex.dto.OrderDTO;
import io.github.xiaoshicae.extension.spring.sample.complex.dto.OrderTagDTO;
import io.github.xiaoshicae.extension.spring.sample.complex.extension.extpoint.AutoCloseOrderTimeExt;
import io.github.xiaoshicae.extension.spring.sample.complex.extension.extpoint.BuildOrderTagExt;
import io.github.xiaoshicae.extension.spring.sample.complex.extension.extpoint.SkipSendMsgExt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.github.xiaoshicae.extension.spring.boot.autoconfigure.annotation.ExtensionInject;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api")
public class CreateOrderController {

    // 超时未支付自动关单扩展点
    @ExtensionInject
    private AutoCloseOrderTimeExt autoCloseOrderTimeExt;

    // 构建订单tag扩展点(匹配到的business和ability均会执行)
    @ExtensionInject
    private List<BuildOrderTagExt> buildOrderTagExtList;

    // 跳过发送消息扩展点
    @ExtensionInject
    private SkipSendMsgExt skipSendMsgExt;

    @GetMapping("/create_order")
    public OrderDTO CreateOrder(
            @RequestParam("category") String category,
            @RequestParam(value = "secondCategory", required = false) String secondCategory,
            @RequestParam(value = "extra", required = false) String extra
    ) throws Exception {

        // 根据扩展点计算自动关单时间
        Duration duration = autoCloseOrderTimeExt.autoCloseOrderTime();

        // 根据扩展点构建订单tag
        List<OrderTagDTO> orderTagList = new ArrayList<>();
        for (BuildOrderTagExt buildOrderTagExt : buildOrderTagExtList) {
            List<OrderTagDTO> tags = buildOrderTagExt.buildOrderTagExt();
            orderTagList.addAll(tags);
        }

        // 构建订单
        OrderDTO order = OrderDTO.builder().orderId("123").userId("abc").category(category).secondCategory(secondCategory).extra(extra).autoCloseSeconds(duration.toSeconds()).orderTagList(orderTagList).build();

        // 是否发送订单创建消息
        if (!skipSendMsgExt.skipSendMsg()) {
            System.out.println("send create order message ...");
        }

        return order;
    }
}
