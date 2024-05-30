package com.jeequan.jeepay.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jeequan.jeepay.core.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 商户应用表
 * </p>
 *
 * @author [mybatis plus generator]
 * @since 2021-06-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_mch_app")
public class MchApp extends BaseModel {

    private static final long serialVersionUID=1L;

    //gw
    public static final LambdaQueryWrapper<MchApp> gw(){
        return new LambdaQueryWrapper<>();
    }


    /**
     * 应用ID
     */
    @TableId(value = "app_id", type = IdType.INPUT)
    private String appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用手机叼
     */
    private String phone;
    /**
     * 应用邮箱
     */
    private String email;
    /**
     * 商户号
     */
    private String mchNo;

    /**
     * 应用是否分账模式：0-不分账, 1-支付成功按配置自动完成分账, 2-商户手动分账(解冻商户金额)
     */
    private Byte divisionMode;

    /**
     * 应用状态: 0-停用, 1-正常
     */
    private Byte state;

    /**
     * 应用私钥
     */
    private String appSecret;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建者用户ID
     */
    private Long createdUid;

    /**
     * 创建者姓名
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

}
