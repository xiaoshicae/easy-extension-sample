package io.github.xiaoshicae.extension.sample.complex.matchparam;


import io.github.xiaoshicae.extension.core.annotation.MatcherParam;

/**
 * 匹配参数，用于判断扩展点是否生效的参数
 * Business和Ability的match()会接受该参数，并判断是否生效
 */
@MatcherParam
public class MatchParam {
    private String Name;
    private String Value;

    public MatchParam(){}

    public MatchParam(String name, String value) {
        this.Name = name;
        this.Value = value;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
}
