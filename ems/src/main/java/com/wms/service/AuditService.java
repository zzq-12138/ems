package com.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.dto.AbandonRequestDTO;
import com.wms.entity.Audit;
import com.wms.enums.AuditStatusEnum;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ems
 * @since 2025-10-29
 */
public interface AuditService extends IService<Audit> {

    int abandonApply(AbandonRequestDTO abandonRequestDTO);

    boolean review(Integer id, AuditStatusEnum auditStatus);
}
