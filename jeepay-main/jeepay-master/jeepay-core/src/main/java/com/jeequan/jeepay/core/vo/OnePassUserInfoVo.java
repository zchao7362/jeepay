package com.jeequan.jeepay.core.vo;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 一号通用户详情对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OnePassUserInfoVo {

    private static final long serialVersionUID = 1L;

    private String account;

    private String phone;

    private String consume;

    private OnePassUserSmsVo sms;

    private OnePassUserCopyVo copy;

    private OnePassUserQueryVo query;

    private OnePassUserDumpVo dump;




}
