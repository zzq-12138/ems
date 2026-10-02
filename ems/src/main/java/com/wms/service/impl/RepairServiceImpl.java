package com.wms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.common.Result;
import com.wms.dto.RepairRequestDTO;
import com.wms.entity.Broken;
import com.wms.entity.EquipmentBase;
import com.wms.entity.Repair;
import com.wms.enums.EquipmentStatusEnum;
import com.wms.mapper.RepairMapper;
import com.wms.service.BrokenService;
import com.wms.service.EquipmentBaseService;
import com.wms.service.RepairService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ems
 * @since 2025-10-31
 */
@Service
public class RepairServiceImpl extends ServiceImpl<RepairMapper, Repair> implements RepairService {

    @Resource
    RepairMapper repairMapper;

    @Resource
    BrokenService  brokenService;

    @Resource
    EquipmentBaseService equipmentBaseService;

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Result saveRepairRecord(RepairRequestDTO repairRequestDTO) {

        // 先保存维修记录
        Repair repairInfo = new Repair();
        BeanUtils.copyProperties(repairRequestDTO, repairInfo);
        repairInfo.setRepairDate(LocalDateTime.now());
        // 如果前端传入 status，则使用前端的状态；否则默认标记为维修中（FAULT）
        EquipmentStatusEnum targetStatus = repairRequestDTO.getStatus() != null ? repairRequestDTO.getStatus() : EquipmentStatusEnum.FAULT;
        repairInfo.setStatus(targetStatus);
        repairMapper.insert(repairInfo);

        // 再更新报错记录：只更新状态，不覆盖原始报错描述（保留用户最初的损坏原因）
        Broken broken = brokenService.getById(repairRequestDTO.getBrokenId());
        broken.setStatus(targetStatus);
        brokenService.saveOrUpdate(broken);

        // 最后更新本地设备基础信息：只更新状态，不覆盖备注（避免丢失原始备注）
        EquipmentBase equipmentBase = equipmentBaseService.findById(broken.getEqId());
        // 如果维修已完成，则设备恢复为正常，否则标记为故障/维修中
        if (EquipmentStatusEnum.REPAIRED.equals(targetStatus)) {
            equipmentBase.setStatus(EquipmentStatusEnum.NORMAL);
        } else {
            equipmentBase.setStatus(EquipmentStatusEnum.FAULT);
        }
        equipmentBaseService.saveOrUpdate(equipmentBase);

        return Result.suc(repairInfo);
    }
}
