package com.jeequan.jeepay.pay.channel.zftAlipay.payway;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class JJZftInterfaceDTO {
    /**
     * 通用字段1-最大长度500
     */
    private String bizIndex1;
    /**
     * 通用字段2-最大长度500
     */
    private String bizIndex2;
    /**
     * 通用字段3-最大长度500
     */
    private String bizIndex3;
    /**
     * 订单附加信息，在异步通知中原样返回
     */
    private String body;

    /**
     * 订单附加信息，在异步通知中原样返回
     */
    private String merchantId;
    /**
     * 订单附加信息，在异步通知中原样返回
     */
    private String merchantOrderNo;
    /**
     * 订单附加信息，在异步通知中原样返回
     */
    private String quitUrl;
    /**
     * 订单附加信息，在异步通知中原样返回
     */
    private String returnUrl;
    /**
     * 订单附加信息，在异步通知中原样返回
     */
    private String subject;
    /**
     * 订单附加信息，在异步通知中原样返回
     */
    private BigDecimal totalAmount;

    private List<JJZftGoodsDTO> goodsDetailList = null;

}
