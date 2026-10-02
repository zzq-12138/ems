package com.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.wms.enums.EquipmentStatusEnum;
import com.wms.enums.EquipmentTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "Base对象", description = "")
public class EquipmentBase implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "设备名称")
    private String name;

    @ApiModelProperty(value = "设备状态")
    private EquipmentStatusEnum status;

    @ApiModelProperty(value = "设备类别")
    private EquipmentTypeEnum type;

    @ApiModelProperty(value = "设备号")
    private String eqNumber;

    @ApiModelProperty(value = "厂家")
    private String manufacturer;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateDate;

    @ApiModelProperty(value = "备注")
    private String des;

    @ApiModelProperty(value = "购买者ID")
    private Long creatorId;

    @ApiModelProperty(value = "购买者姓名")
    private String creatorName;
}
