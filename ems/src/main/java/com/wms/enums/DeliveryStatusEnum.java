package com.wms.enums;

public enum DeliveryStatusEnum {
    PENDING("待配送"),
    SHIPPED("已发货"),
    DELIVERED("已交付");

    private final String description;

    DeliveryStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
