package com.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.wms.enums.EquipmentStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "Repair维修对象", description = "Broken对象")
public class Repair implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "设备Id")
    private Integer eqId;

    @ApiModelProperty(value = "设备名称")
    private String eqName;

    @ApiModelProperty(value = "发现人id")
    private Integer discovererId;

    @ApiModelProperty(value = "发现人名称")
    private String discovererName;

    @ApiModelProperty(value = "设备号")
    private String eqNumber;

    @ApiModelProperty(value = "维修时间")
    private LocalDateTime repairDate;

    @ApiModelProperty(value = "维修公司")
    private String repairCompany;

    @ApiModelProperty(value = "维修费用")
    private Double repairCost;

    @ApiModelProperty(value = "负责人id")
    private Integer chargeId;

    @ApiModelProperty(value = "负责人名称")
    private String chargeName;

    @ApiModelProperty(value = "设备状态")
    private EquipmentStatusEnum status;

    @ApiModelProperty(value = "维修意见")
    private String des;

    @ApiModelProperty(value = "报错id")
    private Integer brokenId;

    @ApiModelProperty(value = "原始损坏原因")
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String brokenReason;
}
