package com.jeequan.jeepay.pay.channel.zftAlipay.payway;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.core.entity.PayOrder;
import com.jeequan.jeepay.core.model.params.wxpay.WxpayNormalMchParams;
import com.jeequan.jeepay.core.model.params.zftAlipay.ZftAlipayNormalMchParams;
import com.jeequan.jeepay.pay.channel.zftAlipay.ZftAlipayPaymentService;
import com.jeequan.jeepay.pay.model.MchAppConfigContext;
import com.jeequan.jeepay.pay.rqrs.AbstractRS;
import com.jeequan.jeepay.pay.rqrs.msg.ChannelRetMsg;
import com.jeequan.jeepay.pay.rqrs.payorder.UnifiedOrderRQ;
import com.jeequan.jeepay.pay.rqrs.payorder.payway.ZftAliPayOrderRS;
import com.jeequan.jeepay.pay.util.ApiResBuilder;
import com.jeequan.jeepay.service.impl.PayOrderService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service("ZftAlipayPaymentByZftAlipayService")
public class ZftAlipay extends ZftAlipayPaymentService {

    @Override
    public String preCheck(UnifiedOrderRQ rq, PayOrder payOrder) {
        return null;
    }

    @Override
    public AbstractRS pay(UnifiedOrderRQ rq, PayOrder payOrder, MchAppConfigContext mchAppConfigContext) throws IOException {

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


    public static String sendPost(String url, String param, String authorization) throws IOException {
        String charset = "utf-8";
        HttpURLConnection conn = null;
        OutputStreamWriter out = null;
        BufferedReader read = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            conn = (HttpURLConnection)realUrl.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("authorization", authorization);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            out = new OutputStreamWriter(conn.getOutputStream(), charset);
            out.write(param);
            out.flush();

            int responseCode = conn.getResponseCode();
            if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE) {
                read = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
                String line;
                while ((line = read.readLine()) != null) {
                    result.append(line);
                }
            } else {
                throw new IOException("Server returned non-OK status: " + responseCode);
            }
        } finally {
            if (out != null) {
                out.close();
            }
            if (read != null) {
                read.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result.toString();
    }

    @Async
    public void asyncUpdatePayOrder(String  merchantNo,PayOrder payOrder){
        //更新zft商户号
        payOrder.setZftMchNo(merchantNo);
       // payOrderService.saveOrUpdate(payOrder);
    }
}

