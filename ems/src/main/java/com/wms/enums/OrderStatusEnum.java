package com.wms.enums;

public enum OrderStatusEnum {
    PENDING("待处理"),
    CONFIRMED("已确认"),
    SHIPPED("已发货"),
    DELIVERED("已交付"),
    CANCELLED("已取消");

    private final String description;

    OrderStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
