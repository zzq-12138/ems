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
@ApiModel(value = "Fault对象", description = "Fault对象")
public class Fault implements Serializable {
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

    @ApiModelProperty(value = "故障时间")
    private LocalDateTime faultDate;

    @ApiModelProperty(value = "设备状态")
    private EquipmentStatusEnum status;

    @ApiModelProperty(value = "损坏原因")
    private String des;
}

