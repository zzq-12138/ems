package com.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.wms.enums.AuditStatusEnum;
import com.wms.enums.AuditTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "Audit对象", description = "")
@Accessors(chain = true)
public class Audit implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "申请人昵称")
    private String applyUserName;

    @ApiModelProperty(value = "申请人id")
    private Integer applyUserId;

    @ApiModelProperty(value = "审核状态")
    private AuditStatusEnum status;

    @ApiModelProperty(value = "审核时间")
    private LocalDateTime auditDate;

    @ApiModelProperty(value = "申请编号")
    private Integer applyId;

    @ApiModelProperty(value = "申请类型")
    private AuditTypeEnum auditType;

    @ApiModelProperty(value = "拒绝原因")
    private String des;
}
