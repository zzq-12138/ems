package com.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.entity.EquipmentBase;
import com.wms.enums.EquipmentStatusEnum;
import com.wms.enums.EquipmentTypeEnum;
import com.wms.service.EquipmentBaseService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/equipmentBase")
public class EquipmentBaseController {
    @Resource
    private EquipmentBaseService equipmentBaseService;

    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();
        String name = (String)param.get("name");

        // 修复枚举转换问题
        EquipmentStatusEnum status = parseEquipmentStatus(param.get("status"));
        EquipmentTypeEnum type = parseEquipmentType(param.get("type"));

        String eqNumber = (String)param.get("eqNumber");

        // 修复日期转换问题
        LocalDateTime updateDate = parseLocalDateTime(param.get("updateDate"));

        Page<EquipmentBase> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<EquipmentBase> lambdaQueryWrapper = new LambdaQueryWrapper();
        if(StringUtils.isNotBlank(name) && !"null".equals(name)){
            lambdaQueryWrapper.like(EquipmentBase::getName,name);
        }
        if(ObjectUtils.isNotNull(status)){
            lambdaQueryWrapper.eq(EquipmentBase::getStatus,status);
        }
        if(ObjectUtils.isNotNull(type)){
            lambdaQueryWrapper.eq(EquipmentBase::getType,type);
        }
        if(ObjectUtils.isNotNull(updateDate)){
            lambdaQueryWrapper.le(EquipmentBase::getUpdateDate,updateDate);
        }

        // 添加按照 id 从大到小排序（降序）
        lambdaQueryWrapper.orderByDesc(EquipmentBase::getId);

        IPage<EquipmentBase> result = equipmentBaseService.page(page,lambdaQueryWrapper);
        return Result.suc(result.getRecords(),result.getTotal());
    }

    /**
     * 安全转换设备类型枚举
     */
    public EquipmentTypeEnum parseEquipmentType(Object typeObj) {
        if (typeObj == null) {
            return null;
        }

        String typeStr;
        if (typeObj instanceof EquipmentTypeEnum) {
            return (EquipmentTypeEnum) typeObj;
        } else if (typeObj instanceof String) {
            typeStr = (String) typeObj;
        } else {
            typeStr = typeObj.toString();
        }

        if (typeStr.trim().isEmpty() || "null".equalsIgnoreCase(typeStr)) {
            return null;
        }

        // 统一转换为大写并处理常见拼写错误
        String normalized = typeStr.toUpperCase().trim();

        switch (normalized) {
            case "KRYBOARD":
                return EquipmentTypeEnum.KEYBOARD;
            case "MOUSE":
                return EquipmentTypeEnum.MOUSE;
            case "PCPC":
                return EquipmentTypeEnum.PC;
            case "DISPLAY":
                return EquipmentTypeEnum.DISPLAY;
            case "KEYBOARD":
                return EquipmentTypeEnum.KEYBOARD;
            default:
                // 如果都不匹配，尝试直接转换（可能会抛出异常，但这是预期的）
                try {
                    return EquipmentTypeEnum.valueOf(normalized);
                } catch (IllegalArgumentException e) {
                    // 记录日志或返回null，根据业务需求决定
                    return null;
                }
        }
    }

    /**
     * 安全转换设备状态枚举
     */
    public EquipmentStatusEnum parseEquipmentStatus(Object statusObj) {
        if (statusObj == null) {
            return null;
        }

        String statusStr;
        if (statusObj instanceof EquipmentStatusEnum) {
            return (EquipmentStatusEnum) statusObj;
        } else if (statusObj instanceof String) {
            statusStr = (String) statusObj;
        } else {
            statusStr = statusObj.toString();
        }

        if (statusStr.trim().isEmpty() || "null".equalsIgnoreCase(statusStr)) {
            return null;
        }

        // 统一转换为大写
        String normalized = statusStr.toUpperCase().trim();

        try {
            return EquipmentStatusEnum.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            // 记录日志或返回null，根据业务需求决定
            return null;
        }
    }

    /**
     * 安全转换日期时间
     */
    public LocalDateTime parseLocalDateTime(Object dateObj) {
        if (dateObj == null) {
            return null;
        }

        if (dateObj instanceof LocalDateTime) {
            return (LocalDateTime) dateObj;
        } else if (dateObj instanceof String) {
            String dateStr = (String) dateObj;
            if (dateStr.trim().isEmpty() || "null".equalsIgnoreCase(dateStr)) {
                return null;
            }

            try {
                // 根据你的日期格式进行调整
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return LocalDateTime.parse(dateStr, formatter);
            } catch (Exception e) {
                // 如果解析失败，尝试其他格式或返回null
                return null;
            }
        }

        return null;
    }

    @PostMapping("/save")
    public Result save(@RequestBody EquipmentBase equipmentBase){
        try{
            System.out.println("[EquipmentBaseController.save] payload: " + equipmentBase);
            if(Objects.isNull(equipmentBase)){
                return Result.fail("参数为空");
            }

            // 必须有 type
            EquipmentTypeEnum type = equipmentBase.getType();
            if(type == null){
                // 尝试从可能的字符串转换
                type = parseEquipmentType(equipmentBase.getType());
            }
            if(type == null){
                return Result.fail("type 参数无效");
            }

            // 设置默认状态
            if(equipmentBase.getStatus() == null){
                equipmentBase.setStatus(EquipmentStatusEnum.NORMAL);
            }

            // 生成按类型顺序的 eqNumber
            int seq = getMaxEquipmentSequence(type) + 1;
            String eqNumber = generateEquipmentNumber(type, seq);
            equipmentBase.setEqNumber(eqNumber);

            // 更新时间为当前时间
            equipmentBase.setUpdateDate(LocalDateTime.now());

            equipmentBase.setType(type);

            // 校验 manufacturer 长度（如果前端传入）
            String manufacturer = equipmentBase.getManufacturer();
            if (manufacturer != null && manufacturer.length() > 20) {
                return Result.fail("manufacturer 长度不能超过20");
            }

            boolean ok = equipmentBaseService.save(equipmentBase);
            if(ok){
                return Result.suc(equipmentBase);
            }else{
                return Result.fail();
            }
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail("服务器异常: " + e.getMessage());
        }
    }

    @PostMapping("/saveBatchByCount")
    public Result saveBatchByCount(@RequestBody HashMap<String,Object> payload){
        try{
            if(payload == null) return Result.fail("参数为空");

            String name = payload.get("name") == null ? null : payload.get("name").toString();
            Object typeObj = payload.get("type");
            Object countObj = payload.get("count");

            EquipmentTypeEnum type = parseEquipmentType(typeObj);
            int count = 0;
            try{
                if(countObj != null) count = Integer.parseInt(countObj.toString());
            }catch(Exception e){
                return Result.fail("count 参数无效");
            }

            if(type == null) return Result.fail("type 参数无效");
            if(count <= 0) return Result.fail("count 必须大于0");

            int maxSequence = getMaxEquipmentSequence(type);
            List<EquipmentBase> equipmentList = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();

            // 读取可选的厂家和创建者信息
            Object manufacturerObj = payload.get("manufacturer");
            Object creatorIdObj = payload.get("creatorId");
            Object creatorNameObj = payload.get("creatorName");
            String manufacturer = manufacturerObj == null ? null : manufacturerObj.toString();
            if (manufacturer != null && manufacturer.length() > 20) {
                return Result.fail("manufacturer 长度不能超过20");
            }
            Long creatorId = null;
            if(creatorIdObj != null){
                try{ creatorId = Long.parseLong(creatorIdObj.toString()); }catch(Exception ex){ creatorId = null; }
            }
            String creatorName = creatorNameObj == null ? null : creatorNameObj.toString();

            for(int i=1;i<=count;i++){
                EquipmentBase eq = new EquipmentBase();
                eq.setName(name);
                eq.setStatus(EquipmentStatusEnum.NORMAL);
                eq.setType(type);
                eq.setUpdateDate(now);

                int seq = maxSequence + i;
                String eqNumber = generateEquipmentNumber(type, seq);
                eq.setEqNumber(eqNumber);

                if(manufacturer != null) eq.setManufacturer(manufacturer);
                if(creatorId != null) eq.setCreatorId(creatorId);
                if(creatorName != null) eq.setCreatorName(creatorName);

                equipmentList.add(eq);
            }

            boolean saved = equipmentBaseService.saveBatch(equipmentList);
            if(saved){
                return Result.suc(equipmentList);
            }else{
                return Result.fail("保存失败");
            }
        }catch(Exception e){
            e.printStackTrace();
            return Result.fail("服务器异常: " + e.getMessage());
        }
    }

    private int getMaxEquipmentSequence(EquipmentTypeEnum equipmentType) {
        QueryWrapper<EquipmentBase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", equipmentType.name());
        queryWrapper.orderByDesc("eq_number");
        queryWrapper.last("LIMIT 1");

        EquipmentBase lastEquipment = equipmentBaseService.getOne(queryWrapper);
        if (lastEquipment == null || lastEquipment.getEqNumber() == null) {
            return 0;
        }

        String eqNumber = lastEquipment.getEqNumber();
        try {
            String[] parts = eqNumber.split("-");
            if (parts.length >= 2) {
                return Integer.parseInt(parts[1]);
            }
        } catch (NumberFormatException e) {
            // 忽略解析错误
        }
        return 0;
    }

    private String generateEquipmentNumber(EquipmentTypeEnum equipmentType, int sequence) {
        return equipmentType.name() + "-" + sequence;
    }
}