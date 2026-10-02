package com.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.wms.enums.EquipmentTypeEnum;
import com.wms.enums.PurchaseStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "购买申请对象", description = "")
public class PurchaseApply implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "设备名称")
    private String name;

    @ApiModelProperty(value = "审批结果")
    private PurchaseStatusEnum status;

    @ApiModelProperty(value = "设备数量")
    private Integer count;

//    @ApiModelProperty(value = "设备尾号")
//    private Integer num;

    @ApiModelProperty(value = "设备类型")
    private EquipmentTypeEnum type;

    @ApiModelProperty(value = "单价")
    private Double unitPrice;

//    @ApiModelProperty(value = "设备号")
//    private String eqNumber;

    @ApiModelProperty(value = "购买时间")
    private LocalDateTime purchaseDate;

    @ApiModelProperty(value = "厂家名称")
    private String company;

    @ApiModelProperty(value = "购买人id")
    private Integer purchaseId;

    @ApiModelProperty(value = "购买人名称")
    private String purchaseName;

    @ApiModelProperty(value = "申请人id")
    private Integer requesterId;

    @ApiModelProperty(value = "申请人名称")
    private String requesterName;

    @ApiModelProperty(value = "申请时间")
    private LocalDateTime createDate;

    @ApiModelProperty(value = "申请理由")
    @TableField("des")
    private String applyReason;

    @ApiModelProperty(value = "审核意见")
    @TableField("audit_opinion")
    private String des;
    public LocalDateTime getCreateTime() {
        return LocalDateTime.now();
    }

}
