package io.github.xiaoshicae.extension.nonspring.sample.extension.extpoint;


import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;

// 跳过发送消息扩展点
@ExtensionPoint
public interface SkipSendMsgExt {
    boolean skipSendMsg();
}
