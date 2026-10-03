package com.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.dto.AbandonRequestDTO;
import com.wms.entity.Audit;
import com.wms.enums.AuditStatusEnum;
import com.wms.enums.AuditTypeEnum;
import com.wms.service.AuditService;
import com.wms.utils.TransformUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ems
 * @since 2025-11-2
 */
@RestController
@RequestMapping("/audit")
public class AuditController {
    @Resource
    private AuditService auditService;

    @Resource
    private TransformUtil transformUtil;

    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();
        String applyUserName = (String)param.get("applyUserName");
        LocalDateTime auditDate = transformUtil.parseLocalDateTime(param.get("auditDate"));
        AuditStatusEnum status = transformUtil.parseAuditStatus(param.get("status"));
        AuditTypeEnum auditType = transformUtil.parseAuditTypeEnum(param.get("auditType"));

        Page<Audit> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Audit> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(applyUserName) && !"null".equals(applyUserName)){
            lambdaQueryWrapper.like(Audit::getApplyUserName,applyUserName);
        }
        if(ObjectUtils.isNotNull(auditDate)){
            lambdaQueryWrapper.le(Audit::getAuditDate,auditDate);
        }
        if(ObjectUtils.isNotNull(status)){
            lambdaQueryWrapper.eq(Audit::getStatus,status);
        }
        if(ObjectUtils.isNotNull(auditType)){
            lambdaQueryWrapper.eq(Audit::getAuditType,auditType);
        }

        lambdaQueryWrapper.orderByDesc(Audit::getId);

        IPage<Audit> result = auditService.page(page,lambdaQueryWrapper);
        return Result.suc(result.getRecords(),result.getTotal());

    }

    @PostMapping("/abandonApply")
    public Result abandonApply(@RequestBody AbandonRequestDTO abandonRequestDTO){
        return Result.suc(auditService.abandonApply(abandonRequestDTO));
    }

    @GetMapping("/review/{id}")
    public Result review(@PathVariable Integer id, @RequestParam AuditStatusEnum auditStatus){
        return auditService.review(id, auditStatus) ? Result.suc() : Result.fail("审核记录不存在");
    }
}
