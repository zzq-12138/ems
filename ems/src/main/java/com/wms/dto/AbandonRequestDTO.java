package com.wms.dto;

import com.wms.enums.EquipmentStatusEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AbandonRequestDTO {

    private Integer id;
    private Integer eqId; // 设备ID
    private String eqName; // 设备名称
    private String eqNumber; // 设备号
    private String discovererName; // 发现人姓名
    private LocalDateTime brokenDate; // 损坏时间
    private EquipmentStatusEnum status; // 状态
    private String des; // 损坏描述
    private Integer applyUserId; // 申请人ID
    private String applyUserName; // 申请人姓名


    // 其他字段根据前端传递的row中的字段来定，这里只是示例

    // getter和setter
}
