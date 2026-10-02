package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.common.Result;
import com.wms.dto.OrderListDTO;
import com.wms.dto.OrderQueryDTO;
import com.wms.entity.Delivery;
import com.wms.entity.Order;
import com.wms.entity.OrderItem;
import com.wms.mapper.DeliveryMapper;
import com.wms.mapper.OrderItemMapper;
import com.wms.mapper.OrderMapper;
import com.wms.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ems
 * @since 2026-03-30
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private DeliveryMapper deliveryMapper;

    @Override
    public Result getOrderList(OrderQueryDTO queryDTO) {
        try {
            // 构建查询条件
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            if (queryDTO.getUserId() != null) {
                queryWrapper.eq("user_id", queryDTO.getUserId());
            }
            if (queryDTO.getStatus() != null) {
                queryWrapper.eq("status", queryDTO.getStatus());
            }
            if (queryDTO.getStartDate() != null) {
                queryWrapper.ge("order_date", queryDTO.getStartDate());
            }
            if (queryDTO.getEndDate() != null) {
                queryWrapper.le("order_date", queryDTO.getEndDate());
            }
            queryWrapper.orderByDesc("create_time");

            // 分页查询订单
            Page<Order> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
            IPage<Order> orderPage = orderMapper.selectPage(page, queryWrapper);

            // 获取订单ID列表
            List<Long> orderIds = new ArrayList<>();
            for (Order order : orderPage.getRecords()) {
                orderIds.add(order.getId());
            }

            // 批量查询订单项
            List<OrderItem> allItems = new ArrayList<>();
            if (!orderIds.isEmpty()) {
                QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
                itemWrapper.in("order_id", orderIds);
                allItems = orderItemMapper.selectList(itemWrapper);
            }

            // 批量查询配送信息
            List<Delivery> allDeliveries = new ArrayList<>();
            if (!orderIds.isEmpty()) {
                QueryWrapper<Delivery> deliveryWrapper = new QueryWrapper<>();
                deliveryWrapper.in("order_id", orderIds);
                allDeliveries = deliveryMapper.selectList(deliveryWrapper);
            }

            // 组装数据
            List<OrderListDTO> orderList = new ArrayList<>();
            for (Order order : orderPage.getRecords()) {
                OrderListDTO dto = new OrderListDTO();
                dto.setOrder(order);

                // 过滤该订单的订单项
                List<OrderItem> items = new ArrayList<>();
                for (OrderItem item : allItems) {
                    if (item.getOrderId().equals(order.getId())) {
                        items.add(item);
                    }
                }
                dto.setItems(items);

                // 过滤该订单的配送信息（假设一个订单一个配送）
                for (Delivery delivery : allDeliveries) {
                    if (delivery.getOrderId().equals(order.getId())) {
                        dto.setDelivery(delivery);
                        break;
                    }
                }

                orderList.add(dto);
            }

            return Result.suc(orderList, orderPage.getTotal());
        } catch (Exception e) {
            log.error("查询订单列表失败", e);
            return Result.fail("查询失败：" + e.getMessage());
        }
    }
}
