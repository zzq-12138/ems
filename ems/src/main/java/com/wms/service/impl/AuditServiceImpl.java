package com.wms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.dto.AbandonRequestDTO;
import com.wms.entity.Audit;
import com.wms.entity.Broken;
import com.wms.entity.EquipmentBase;
import com.wms.enums.AuditStatusEnum;
import com.wms.enums.AuditTypeEnum;
import com.wms.enums.EquipmentStatusEnum;
import com.wms.mapper.AuditMapper;
import com.wms.service.AuditService;
import com.wms.service.BrokenService;
import com.wms.service.EquipmentBaseService;
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
 * @since 2025-10-29
 */
@Service
public class AuditServiceImpl extends ServiceImpl<AuditMapper, Audit> implements AuditService {

    @Resource
    AuditMapper auditMapper;

    @Resource
    BrokenService brokenService;

    @Resource
    EquipmentBaseService equipmentBaseService;

    @Override
    public int abandonApply(AbandonRequestDTO abandonRequestDTO) {
        Audit auditInfo = new Audit().setAuditType(AuditTypeEnum.ABANDONED)
                .setApplyId(abandonRequestDTO.getId())
                .setApplyUserName(abandonRequestDTO.getApplyUserName())
                .setStatus(AuditStatusEnum.TO_BE_REVIEWED)
                .setAuditType(AuditTypeEnum.ABANDONED)
                .setDes(abandonRequestDTO.getDes());
        int rows = auditMapper.insert(auditInfo);

        // 标记对应的 broken 记录为待审核状态，避免前端再次显示可废弃按钮
        if (abandonRequestDTO.getId() != null) {
            Broken broken = brokenService.getById(abandonRequestDTO.getId());
            if (broken != null) {
                broken.setStatus(EquipmentStatusEnum.TO_BE_AUDIT);
                brokenService.saveOrUpdate(broken);

                // 同时更新设备基础状态为待审核
                EquipmentBase equipmentBase = equipmentBaseService.findById(broken.getEqId());
                if (equipmentBase != null) {
                    equipmentBase.setStatus(EquipmentStatusEnum.TO_BE_AUDIT);
                    equipmentBaseService.saveOrUpdate(equipmentBase);
                }
            }
        }

        return rows;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean review(Integer id, AuditStatusEnum auditStatus) {
        if (AuditStatusEnum.PASS.equals(auditStatus)){

            // 先更新当前申请的状态
            Audit audit = auditMapper.selectById(id);
            if (audit == null) {
                return false;
            }
            audit.setStatus(AuditStatusEnum.PASS)
                    .setAuditDate(LocalDateTime.now());
            auditMapper.updateById(audit);

            // 再更新损坏记录状态
            Broken broken = brokenService.getById(audit.getApplyId());
            if (broken != null) {
                broken.setStatus(EquipmentStatusEnum.ABANDONED);
                brokenService.saveOrUpdate(broken);

                // 再更新设备基础状态
                EquipmentBase equipmentBase = equipmentBaseService.findById(broken.getEqId());
                if (equipmentBase != null) {
                    equipmentBase.setStatus(EquipmentStatusEnum.ABANDONED);
                    equipmentBaseService.saveOrUpdate(equipmentBase);
                }
            }

        }else if (AuditStatusEnum.REJECT.equals(auditStatus)) {

            // 先更新当前申请的状态
            Audit audit = auditMapper.selectById(id);
            if (audit == null) {
                return false;
            }
            audit.setStatus(AuditStatusEnum.REJECT)
                    .setAuditDate(LocalDateTime.now());
            auditMapper.updateById(audit);

            // 再更新损坏记录状态
            Broken broken = brokenService.getById(audit.getApplyId());
            if (broken != null) {
                broken.setStatus(EquipmentStatusEnum.FAULT);
                brokenService.saveOrUpdate(broken);

                // 再更新设备基础状态
                EquipmentBase equipmentBase = equipmentBaseService.findById(broken.getEqId());
                if (equipmentBase != null) {
                    equipmentBase.setStatus(EquipmentStatusEnum.FAULT);
                    equipmentBaseService.saveOrUpdate(equipmentBase);
                }
            }

        }else {
            throw new RuntimeException("申请状态有误，请核实申请状态");
        }
        return true;
    }
}
