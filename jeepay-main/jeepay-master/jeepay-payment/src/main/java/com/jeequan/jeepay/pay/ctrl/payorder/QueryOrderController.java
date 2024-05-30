/*
 * Copyright (c) 2021-2031, 河北计全科技有限公司 (https://www.jeequan.com & jeequan@126.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jeequan.jeepay.pay.ctrl.payorder;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.jeequan.jeepay.core.constants.ApiCodeEnum;
import com.jeequan.jeepay.core.entity.MchApp;
import com.jeequan.jeepay.core.entity.PayOrder;
import com.jeequan.jeepay.core.exception.BizException;
import com.jeequan.jeepay.core.model.ApiRes;
import com.jeequan.jeepay.core.utils.JeepayKit;
import com.jeequan.jeepay.pay.ctrl.ApiController;
import com.jeequan.jeepay.pay.model.MchAppConfigContext;
import com.jeequan.jeepay.pay.rqrs.payorder.CallbackOrderRQ;
import com.jeequan.jeepay.pay.rqrs.payorder.QueryPayOrderRQ;
import com.jeequan.jeepay.pay.rqrs.payorder.QueryPayOrderRS;
import com.jeequan.jeepay.pay.service.ConfigContextQueryService;
import com.jeequan.jeepay.service.impl.PayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
* 商户查单controller
*
* @author terrfly
* @site https://www.jeequan.com
* @date 2021/6/8 17:26
*/
@Slf4j
@RestController
public class QueryOrderController extends ApiController {

    @Autowired private PayOrderService payOrderService;
    @Autowired private ConfigContextQueryService configContextQueryService;

    /**
     * 查单接口
     * **/
    @RequestMapping("/api/pay/query")
    public ApiRes queryOrder(){

        //获取参数 & 验签
        QueryPayOrderRQ rq = getRQByWithMchSign(QueryPayOrderRQ.class);

        if(StringUtils.isAllEmpty(rq.getMchOrderNo(), rq.getPayOrderId())){
            throw new BizException("mchOrderNo 和 payOrderId不能同时为空");
        }

        PayOrder payOrder = payOrderService.queryMchOrder(rq.getMchNo(), rq.getPayOrderId(), rq.getMchOrderNo());
        if(payOrder == null){
            throw new BizException("订单不存在");
        }

        QueryPayOrderRS bizRes = QueryPayOrderRS.buildByPayOrder(payOrder);
        return ApiRes.okWithSign(bizRes, configContextQueryService.queryMchApp(rq.getMchNo(), rq.getAppId()).getAppSecret());
    }

    /**
     * 回调接口
     * **/
    @RequestMapping("/api/pay/callbackOrder")
    public ApiRes callbackOrder(){
        //获取参数 & 验签
        CallbackOrderRQ rq = getRQByWithCallbackSign(CallbackOrderRQ.class);
        if(StringUtils.isAllEmpty(rq.getMchOrderNo(), rq.getPayOrderId())){
            throw new BizException("mchOrderNo 和 payOrderId不能同时为空");
        }
        PayOrder payOrder = payOrderService.queryZftOrder(rq.getMchNo(), rq.getPayOrderId(), rq.getMchOrderNo());
        if(payOrder == null){
            throw new BizException("订单不存在");
        }
        payOrder.setState(rq.getState());
        payOrderService.saveOrUpdate(payOrder);
        //异步回调上游接口
        callbackOrderUp(payOrder);
        return ApiRes.ok();
    }

    /**
     * 回调上游平台
     * **/
    @Async
    public void callbackOrderUp(PayOrder payOrder ){
        payOrderService.callbackOrder(payOrder);
    }
}
