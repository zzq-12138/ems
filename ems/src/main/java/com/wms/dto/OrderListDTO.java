package com.wms.dto;

import com.wms.entity.Delivery;
import com.wms.entity.Order;
import com.wms.entity.OrderItem;
import lombok.Data;

import java.util.List;

@Data
public class OrderListDTO {
    private Order order;
    private List<OrderItem> items;
    private Delivery delivery;
}
