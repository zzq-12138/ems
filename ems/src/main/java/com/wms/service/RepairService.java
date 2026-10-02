package com.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.common.Result;
import com.wms.dto.RepairRequestDTO;
import com.wms.entity.Repair;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ems
 * @since 2025-10-30
 */
public interface RepairService extends IService<Repair> {

    Result saveRepairRecord(RepairRequestDTO repairRequestDTO);
}
