package com.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ems
 * @since 2026-03-30
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}
