package com.wms.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.wms.enums.EquipmentStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class RepairRequestDTO {
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

    @ApiModelProperty(value = "维修公司")
    private String repairCompany;

    @ApiModelProperty(value = "维修费用")
    private Double repairCost;

    @ApiModelProperty(value = "负责人id")
    private Integer chargeId;

    @ApiModelProperty(value = "负责人名称")
    private String chargeName;

    @ApiModelProperty(value = "损坏时间")
    private LocalDateTime brokenDate;

    @ApiModelProperty(value = "设备状态")
    private EquipmentStatusEnum status;

    @ApiModelProperty(value = "损坏原因")
    private String des;

    @ApiModelProperty(value = "报错id")
    private Integer brokenId;
}
