package com.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.dto.RepairRequestDTO;
import com.wms.entity.Repair;
import com.wms.enums.EquipmentStatusEnum;
import com.wms.service.RepairService;
import com.wms.utils.TransformUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ems
 * @since 2025-10-31
 */
@RestController
@RequestMapping("/repair")
public class RepairController {

    @Resource
    TransformUtil transformUtil;

    @Resource
    RepairService repairService;
    @Resource
    com.wms.service.BrokenService brokenService;
    
    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();
        String eqName = (String)param.get("eqName");
        String eqNumber = (String)param.get("eqNumber");
        LocalDateTime repairDate = transformUtil.parseLocalDateTime(param.get("repairDate"));
        EquipmentStatusEnum status = transformUtil.parseEquipmentStatus(param.get("status"));


        Page<Repair> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Repair> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(eqName) && !"null".equals(eqName)){
            lambdaQueryWrapper.like(Repair::getEqName,eqName);
        }
        if(StringUtils.isNotBlank(eqNumber) && !"null".equals(eqNumber)){
            lambdaQueryWrapper.like(Repair::getEqNumber,eqNumber);
        }
        if(ObjectUtils.isNotNull(repairDate)){
            lambdaQueryWrapper.le(Repair::getRepairDate,repairDate);
        }
        if(ObjectUtils.isNotNull(status)){
            lambdaQueryWrapper.eq(Repair::getStatus,status);
        } else {
            // По умолчанию показываем только записи в процессе ремонта и уже отремонтированные
            lambdaQueryWrapper.in(Repair::getStatus, EquipmentStatusEnum.FAULT, EquipmentStatusEnum.REPAIRED);
        }

        // 添加按照 id 从大到小排序（降序）
        lambdaQueryWrapper.orderByDesc(Repair::getId);

        // 获取符合条件的所有 repair 记录（后面在 Java 端与 broken 合并并分页）
        java.util.List<Repair> repairList = repairService.list(lambdaQueryWrapper);
        // 填充原始损坏原因（从 broken 表）
        for (Repair r : repairList) {
            if (r.getBrokenId() != null) {
                com.wms.entity.Broken b = brokenService.getById(r.getBrokenId());
                if (b != null) {
                    r.setBrokenReason(b.getDes());
                }
            }
        }

        // 另外查询未修复的 broken 报告并合并显示（使用户提交的报障能在维修页看到）
        // 当前如果前端没有传 status，则默认合并状态为 BROKEN 和 TO_BE_AUDIT
        java.util.List<Repair> merged = new java.util.ArrayList<>();
        // 先将 broken 报告组成 Repair 视图对象并加入列表头部
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.wms.entity.Broken> brokenQ = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(eqName) && !"null".equals(eqName)){
            brokenQ.like(com.wms.entity.Broken::getEqName, eqName);
        }
        if(StringUtils.isNotBlank(eqNumber) && !"null".equals(eqNumber)){
            brokenQ.like(com.wms.entity.Broken::getEqNumber, eqNumber);
        }
        if(ObjectUtils.isNotNull(status)){
            // 如果前端指定了 status，则以此为准
            brokenQ.eq(com.wms.entity.Broken::getStatus, status);
        } else {
            // По умолчанию НЕ включаем простые "BROKEN" заявки — показываем только те, которые относятся к ремонту/已维修
            brokenQ.in(com.wms.entity.Broken::getStatus, EquipmentStatusEnum.FAULT, EquipmentStatusEnum.REPAIRED);
        }
        brokenQ.orderByDesc(com.wms.entity.Broken::getId);
        java.util.List<com.wms.entity.Broken> brokenList = brokenService.list(brokenQ);
        for (com.wms.entity.Broken b : brokenList) {
            Repair r = new Repair();
            r.setId(b.getId());
            r.setEqId(b.getEqId());
            r.setEqName(b.getEqName());
            r.setEqNumber(b.getEqNumber());
            r.setDiscovererId(b.getDiscovererId());
            r.setDiscovererName(b.getDiscovererName());
            r.setRepairDate(b.getBrokenDate());
            r.setStatus(b.getStatus());
            r.setDes(b.getDes());
            r.setBrokenId(b.getId());
            r.setBrokenReason(b.getDes());
            merged.add(r);
        }

        // 然后追加已有的 repair 记录
        merged.addAll(repairList);

        // 全局按 id 降序排序，然后在 Java 端做分页返回子集
        merged.sort((a, b) -> {
            if (a.getId() == null && b.getId() == null) return 0;
            if (a.getId() == null) return 1;
            if (b.getId() == null) return -1;
            return b.getId().compareTo(a.getId());
        });

        int pageNum = (int) page.getCurrent();
        int pageSize = (int) page.getSize();
        int fromIndex = Math.max(0, (pageNum - 1) * pageSize);
        int toIndex = Math.min(merged.size(), fromIndex + pageSize);
        java.util.List<Repair> pageRecords = new java.util.ArrayList<>();
        if (fromIndex < toIndex) {
            pageRecords = merged.subList(fromIndex, toIndex);
        }

        long total = merged.size();
        return Result.suc(pageRecords, total);

    }

    @PostMapping("/save")
    public Result saveRepairRecord(@RequestBody RepairRequestDTO repairRequestDTO){
        return repairService.saveRepairRecord(repairRequestDTO);
    }
}
