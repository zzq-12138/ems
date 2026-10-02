package com.wms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.entity.EquipmentBase;
import com.wms.mapper.EquipmentBaseMapper;
import com.wms.service.EquipmentBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ems
 * @since 2025-10-28
 */
@Service
public class EquipmentBaseServiceImpl extends ServiceImpl<EquipmentBaseMapper, EquipmentBase> implements EquipmentBaseService {

    @Resource
    EquipmentBaseMapper equipmentBaseMapper;

    @Override
    public EquipmentBase findById(Integer eqId) {
        return equipmentBaseMapper.selectById(eqId);
    }
}
