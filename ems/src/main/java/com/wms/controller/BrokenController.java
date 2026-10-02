package com.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.entity.Broken;
import com.wms.entity.EquipmentBase;
import com.wms.enums.EquipmentStatusEnum;
import com.wms.service.BrokenService;
import com.wms.service.EquipmentBaseService;
import com.wms.utils.TransformUtil;
import org.springframework.transaction.annotation.Transactional;
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
 * @since 2025-11-1
 */
@RestController
@RequestMapping("/broken")
public class BrokenController {

    @Resource
    private BrokenService brokenService;

    @Resource
    private EquipmentBaseService equipmentBaseService;

    @Resource
    private TransformUtil transformUtil;

    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();
        String eqName = (String)param.get("eqName");
        String eqNumber = (String)param.get("eqNumber");
        LocalDateTime brokenDate = transformUtil.parseLocalDateTime(param.get("brokenDate"));
        EquipmentStatusEnum status = transformUtil.parseEquipmentStatus(param.get("status"));


        Page<Broken> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Broken> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(eqName) && !"null".equals(eqName)){
            lambdaQueryWrapper.like(Broken::getEqName,eqName);
        }
        if(StringUtils.isNotBlank(eqNumber) && !"null".equals(eqNumber)){
            lambdaQueryWrapper.like(Broken::getEqNumber,eqNumber);
        }
        if(ObjectUtils.isNotNull(brokenDate)){
            lambdaQueryWrapper.le(Broken::getBrokenDate,brokenDate);
        }
        if(ObjectUtils.isNotNull(status)){
            lambdaQueryWrapper.eq(Broken::getStatus,status);
        }

        // 添加按照 id 从大到小排序（降序）
        lambdaQueryWrapper.orderByDesc(Broken::getId);

        IPage<Broken> result = brokenService.page(page,lambdaQueryWrapper);
        return Result.suc(result.getRecords(),result.getTotal());

    }

    @PostMapping("/save")
    @Transactional(rollbackFor = RuntimeException.class)
    public Result save(@RequestBody Broken broken){

        broken.setStatus(broken.getStatus());
        broken.setBrokenDate(LocalDateTime.now());
        EquipmentBase equipmentBase = equipmentBaseService.findById(broken.getEqId());
        equipmentBase.setStatus(broken.getStatus());
        equipmentBase.setDes(broken.getDes());
        equipmentBaseService.saveOrUpdate(equipmentBase);
        return brokenService.save(broken)?Result.suc():Result.fail();
    }

    @GetMapping("/view/{id}")
    public Result view(@PathVariable Integer id){
        return Result.suc(brokenService.view(id));
    }

}
