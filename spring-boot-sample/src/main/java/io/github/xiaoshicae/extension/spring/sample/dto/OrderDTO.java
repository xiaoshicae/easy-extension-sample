package io.github.xiaoshicae.extension.spring.sample.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderDTO {
    // 订单id
    private String orderId;
    private String userId;
    private String category;
    private String secondCategory;
    private String extra;
    // 超时未支付，自动关单时间
    private Long autoCloseSeconds;
    // 订单tag
    private List<OrderTagDTO> orderTagList;
}
