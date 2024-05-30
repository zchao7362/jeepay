package com.jeequan.jeepay.pay.channel.zftAlipay.payway;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class JJZftGoodsDTO {
    /**
     * 支付宝定义的统一商品编号 最大长度32位
     */
    private String alipayGoodsId;
    /**
     * 商品类目树
     */
    private String categoriesTree;
    /**
     * 商品类目 最大长度24位
     */
    private String goodsCategory;
    /**
     * 商品的编号 最大长度64位
     */
    private String goodsId;
    /**
     * 商品名称 最大长度256
     */
    private String goodsName;
    /**
     * 商品单价，单位为元
     */
    private BigDecimal price;
    /**
     * 商品数量, 支持小数，精确到小数点后两位
     */
    private BigDecimal quantity;
    /**
     * 商品的展示地址 最大长度400位
     */
    private String showUrl;

}
