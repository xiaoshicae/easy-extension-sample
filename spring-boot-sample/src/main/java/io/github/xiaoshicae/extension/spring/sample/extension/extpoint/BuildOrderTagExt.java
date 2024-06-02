package io.github.xiaoshicae.extension.spring.sample.extension.extpoint;


import io.github.xiaoshicae.extension.spring.sample.dto.OrderTagDTO;
import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;

import java.util.List;

// 构建订单标签扩展点
@ExtensionPoint
public interface BuildOrderTagExt {
    List<OrderTagDTO> buildOrderTagExt();
}
