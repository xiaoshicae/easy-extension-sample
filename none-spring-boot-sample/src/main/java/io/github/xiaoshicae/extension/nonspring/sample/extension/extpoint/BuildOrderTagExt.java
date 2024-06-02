package io.github.xiaoshicae.extension.nonspring.sample.extension.extpoint;


import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;
import io.github.xiaoshicae.extension.nonspring.sample.dto.OrderTagDTO;

import java.util.List;

// 构建订单标签扩展点
@ExtensionPoint
public interface BuildOrderTagExt {
    List<OrderTagDTO> buildOrderTagExt();
}
