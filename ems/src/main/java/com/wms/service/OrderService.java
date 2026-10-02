package com.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.common.Result;
import com.wms.dto.OrderQueryDTO;
import com.wms.entity.Order;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ems
 * @since 2026-03-30
 */
public interface OrderService extends IService<Order> {

    Result getOrderList(OrderQueryDTO queryDTO);
}
