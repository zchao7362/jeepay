package com.jeequan.jeepay.pay.channel.zftAlipay;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.core.entity.PayOrder;
import com.jeequan.jeepay.core.model.params.zftAlipay.ZftAlipayNormalMchParams;
import com.jeequan.jeepay.pay.channel.AbstractPaymentService;
import com.jeequan.jeepay.pay.channel.ysfpay.YsfpayPaymentService;
import com.jeequan.jeepay.pay.channel.zftAlipay.payway.JJZftGoodsDTO;
import com.jeequan.jeepay.pay.channel.zftAlipay.payway.JJZftInterfaceDTO;
import com.jeequan.jeepay.pay.model.MchAppConfigContext;
import com.jeequan.jeepay.pay.rqrs.AbstractRS;
import com.jeequan.jeepay.pay.rqrs.msg.ChannelRetMsg;
import com.jeequan.jeepay.pay.rqrs.payorder.UnifiedOrderRQ;
import com.jeequan.jeepay.pay.rqrs.payorder.payway.ZftAliPayOrderRS;
import com.jeequan.jeepay.pay.util.ApiResBuilder;
import com.jeequan.jeepay.pay.util.PaywayUtil;
import com.jeequan.jeepay.service.impl.PayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ZftAlipayPaymentService extends AbstractPaymentService {


    @Autowired
    private PayOrderService payOrderService;

    @Override
    public String getIfCode() {
        return CS.IF_CODE.ZFTPAY;
    }

    @Override
    public boolean isSupport(String wayCode) {
        return true;
    }

    @Override
    public String preCheck(UnifiedOrderRQ bizRQ, PayOrder payOrder) {
        return null;
    }

    @Override
    public AbstractRS pay(UnifiedOrderRQ bizRQ, PayOrder payOrder, MchAppConfigContext mchAppConfigContext) throws
            Exception {
        //ZftAliPayOrderRQ bizRQ = (ZftAliPayOrderRQ)rq;

        ZftAlipayNormalMchParams normalMchParams = (ZftAlipayNormalMchParams)configContextQueryService.queryNormalMchParams(mchAppConfigContext.getMchNo(), mchAppConfigContext.getAppId(), CS.IF_CODE.ZFTPAY);

        BigDecimal amount = new BigDecimal(payOrder.getAmount()/100);
        // 构造函数响应数据
        ZftAliPayOrderRS res = ApiResBuilder.buildSuccess(ZftAliPayOrderRS.class);
        String merchantNo = String.valueOf(normalMchParams.getMerchantNo());
        String payUrl = String.valueOf(normalMchParams.getPayUrl());
        String zfttype = String.valueOf(normalMchParams.getZftType());
        String authorization = SecureUtil.sha256(merchantNo);
        asyncUpdatePayOrder(merchantNo,payOrder);
        JJZftInterfaceDTO jjZftInterfaceDTO = new JJZftInterfaceDTO();
        List<JJZftGoodsDTO> goodsDetailList = new ArrayList<JJZftGoodsDTO>();
        JJZftGoodsDTO jjZftGoodsDTO = new JJZftGoodsDTO();
        jjZftGoodsDTO.setGoodsId("6626074449ea510f0c763944");
        jjZftGoodsDTO.setGoodsName("购买门票");
        jjZftGoodsDTO .setPrice(amount);
        jjZftGoodsDTO.setQuantity(new BigDecimal(1.00));
        goodsDetailList.add(jjZftGoodsDTO);
        jjZftInterfaceDTO.setGoodsDetailList(goodsDetailList);
        jjZftInterfaceDTO.setMerchantId(merchantNo);
        jjZftInterfaceDTO.setMerchantOrderNo(payOrder.getPayOrderId());
        jjZftInterfaceDTO.setReturnUrl(payOrder.getReturnUrl());
        jjZftInterfaceDTO.setQuitUrl("about:blank");
        jjZftInterfaceDTO.setSubject("购买门票");
        jjZftInterfaceDTO.setTotalAmount(amount);
        String body = JSONUtil.toJsonStr(jjZftInterfaceDTO);
        payUrl = payUrl+"/api/trade/collection/site";
        String result2 = HttpRequest.post(payUrl).header("authorization",authorization).body(body).execute().body();
        String url = "";
        JSONObject json = JSONObject.parseObject(result2);
        if("200".equals(json.getString("resp_code"))){
            JSONObject dataJson = json.getJSONObject("data");
            url = dataJson.getString("url");
        }
        ChannelRetMsg channelRetMsg = new ChannelRetMsg();
        res.setChannelRetMsg(channelRetMsg);
        res.setPayData(url);
        //放置 响应数据
        channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.WAITING);

        return res;
    }

    @Async
    public void asyncUpdatePayOrder(String  merchantNo,PayOrder payOrder){
        //更新zft商户号
        payOrder.setZftMchNo(merchantNo);
        payOrderService.saveOrUpdate(payOrder);
    }
}
