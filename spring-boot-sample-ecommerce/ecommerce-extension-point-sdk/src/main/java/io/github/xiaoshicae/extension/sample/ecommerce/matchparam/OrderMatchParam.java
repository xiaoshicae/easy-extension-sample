package io.github.xiaoshicae.extension.sample.ecommerce.matchparam;

import java.math.BigDecimal;
import java.util.List;

/**
 * 匹配参数，描述一次下单请求的订单特征
 * Business 用 bizCode 判断是不是自己的业务；Ability 用其余属性自行判断这次要不要生效
 * (VIP券看会员等级、包邮看金额和收货省份、急速达看是否加急等)
 * 参数类型由 Matcher&lt;OrderMatchParam&gt; 的泛型推导，无需额外注解
 */
public class OrderMatchParam {
    /**
     * 业务标识，如 retail, fresh, digital
     */
    private String bizCode;
    /**
     * 会员等级: NORMAL, VIP, SVIP
     */
    private String memberLevel = NORMAL_MEMBER;
    /**
     * 订单金额
     */
    private BigDecimal orderAmount = BigDecimal.ZERO;
    /**
     * 商品件数
     */
    private int itemCount;
    /**
     * 收货省份，如 广东省
     */
    private String shippingProvince;
    /**
     * 用户是否勾选了加急配送
     */
    private boolean urgentDelivery;
    /**
     * 订单包含的商品品类: general(标品), fresh(生鲜), digital(数码), custom(定制)
     */
    private List<String> categories = List.of();

    private static final String NORMAL_MEMBER = "NORMAL";

    public OrderMatchParam() {
    }

    public OrderMatchParam(String bizCode, String memberLevel, BigDecimal orderAmount, int itemCount,
                           String shippingProvince, boolean urgentDelivery, List<String> categories) {
        this.bizCode = bizCode;
        this.memberLevel = orNormal(memberLevel);
        this.orderAmount = orZero(orderAmount);
        this.itemCount = itemCount;
        this.shippingProvince = shippingProvince;
        this.urgentDelivery = urgentDelivery;
        this.categories = orEmpty(categories);
    }

    public String getBizCode() { return bizCode; }
    public void setBizCode(String bizCode) { this.bizCode = bizCode; }
    public String getMemberLevel() { return memberLevel; }
    public void setMemberLevel(String memberLevel) { this.memberLevel = orNormal(memberLevel); }
    public BigDecimal getOrderAmount() { return orderAmount; }
    public void setOrderAmount(BigDecimal orderAmount) { this.orderAmount = orZero(orderAmount); }
    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
    public String getShippingProvince() { return shippingProvince; }
    public void setShippingProvince(String shippingProvince) { this.shippingProvince = shippingProvince; }
    public boolean isUrgentDelivery() { return urgentDelivery; }
    public void setUrgentDelivery(boolean urgentDelivery) { this.urgentDelivery = urgentDelivery; }
    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = orEmpty(categories); }

    // 匹配参数在每个 match() 里被反复读取，这里统一兜底，能力无需再判空
    private static String orNormal(String memberLevel) { return memberLevel == null ? NORMAL_MEMBER : memberLevel; }
    private static BigDecimal orZero(BigDecimal amount) { return amount == null ? BigDecimal.ZERO : amount; }
    private static List<String> orEmpty(List<String> categories) { return categories == null ? List.of() : List.copyOf(categories); }
}
