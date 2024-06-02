package io.github.xiaoshicae.extension.nonspring.sample;

import io.github.xiaoshicae.extension.core.*;
import io.github.xiaoshicae.extension.core.exception.ExtensionException;
import io.github.xiaoshicae.extension.nonspring.sample.extension.DefaultAbility;
import io.github.xiaoshicae.extension.nonspring.sample.extension.ability.FreeTrialAbility;
import io.github.xiaoshicae.extension.nonspring.sample.extension.ability.PreSaleAbility;
import io.github.xiaoshicae.extension.nonspring.sample.extension.business.BusinessDeliver;
import io.github.xiaoshicae.extension.nonspring.sample.extension.business.BusinessFilm;
import io.github.xiaoshicae.extension.nonspring.sample.extension.extpoint.AutoCloseOrderTimeExt;
import io.github.xiaoshicae.extension.nonspring.sample.extension.paramdto.ParamDTO;

import java.time.Duration;

public class Application {

    public static void main(String[] args) throws ExtensionException {
        Application m = new Application();

        IExtContext<ParamDTO> extContext = new DefaultExtContext<>();

        // 注册业务和能力
        m.registerBusinessAndAbility(extContext);

        // 处理请求前处理
        m.beforeProcess(extContext);

        // 处理请求
        m.process(extContext);

        // 处理请求后处理
        m.afterProcess(extContext);
    }

    private void process(IExtFactory factory) throws ExtensionException {
        AutoCloseOrderTimeExt firstMatchedExtension = factory.getFirstMatchedExtension(AutoCloseOrderTimeExt.class);
        Duration duration = firstMatchedExtension.autoCloseOrderTime();
        System.out.println("auto close order time: " + duration.getSeconds() + "s");
    }

    private void beforeProcess(ISessionManager<ParamDTO> sessionManager) throws ExtensionException {
        sessionManager.initSession(new ParamDTO("deliver", "presale", "xx"));
    }

    private void afterProcess(ISessionManager<ParamDTO> sessionManager) throws ExtensionException {
        sessionManager.removeSession();
    }

    private void registerBusinessAndAbility(IExtRegister<ParamDTO> register) throws ExtensionException {
        // 注册业务
        register.registerBusiness(new BusinessDeliver());
        register.registerBusiness(new BusinessFilm());

        // 注册能力
        register.registerAbility(new FreeTrialAbility());
        register.registerAbility(new PreSaleAbility());

        // 注册默认兜底能力
        register.registerAbility(new DefaultAbility());
    }
}

