package io.github.xiaoshicae.extension.sample.ecommerce.matchparam;

import io.github.xiaoshicae.extension.core.annotation.MatcherParam;

import java.util.List;

/**
 * 匹配参数，用于判断 Business 和 Ability 是否生效
 */
@MatcherParam
public class OrderMatchParam {
    /**
     * 业务标识，如 retail, fresh, digital
     */
    private String bizCode;
    /**
     * 能力标识列表，如 ["vip", "free-shipping", "rapid"]
     */
    private List<String> abilityCodes;

    public OrderMatchParam() {
    }

    public OrderMatchParam(String bizCode, List<String> abilityCodes) {
        this.bizCode = bizCode;
        this.abilityCodes = abilityCodes;
    }

    public String getBizCode() { return bizCode; }
    public void setBizCode(String bizCode) { this.bizCode = bizCode; }
    public List<String> getAbilityCodes() { return abilityCodes; }
    public void setAbilityCodes(List<String> abilityCodes) { this.abilityCodes = abilityCodes; }
}
