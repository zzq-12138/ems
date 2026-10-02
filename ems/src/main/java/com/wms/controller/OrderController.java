package com.wms.controller;

import com.wms.common.Result;
import com.wms.dto.OrderQueryDTO;
import com.wms.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ems
 * @since 2026-03-30
 */
@Api(tags = "订单管理")
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("查询订单列表")
    @PostMapping("/list")
    public Result getOrderList(@RequestBody OrderQueryDTO queryDTO) {
        return orderService.getOrderList(queryDTO);
    }
}
