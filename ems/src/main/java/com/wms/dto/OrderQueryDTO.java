package com.wms.dto;

import com.wms.enums.OrderStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderQueryDTO {
    private Long userId;
    private OrderStatusEnum status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
