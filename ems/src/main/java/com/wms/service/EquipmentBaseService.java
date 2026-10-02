package com.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.entity.EquipmentBase;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ems
 * @since 2025-10-29
 */
public interface EquipmentBaseService extends IService<EquipmentBase> {

    EquipmentBase findById(Integer eqId);
}
