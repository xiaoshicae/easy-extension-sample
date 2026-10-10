package io.github.xiaoshicae.extension.sample.ecommerce.web;

import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.NotifyExtension;
import io.github.xiaoshicae.extension.spring.boot.autoconfigure.annotation.ExtensionInject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 异步通知
 * 演示 easy-extension.async-propagation：@Async 方法在 Spring 的线程池里执行，不在处理请求的线程上
 */
@Service
public class OrderNotifyService {

    @ExtensionInject
    private NotifyExtension notifyExtension;

    /**
     * 开启 easy-extension.async-propagation 后，任务沿用提交它的请求线程上的业务绑定，
     * 选出的实现与同步调用一致；不开启时，这里调用扩展点会抛 ResolutionException(NO_BINDING)
     */
    @Async
    public CompletableFuture<List<String>> notifyChannelsAsync(String notifyEvent) {
        return CompletableFuture.completedFuture(notifyExtension.getNotifyChannels(notifyEvent));
    }
}
