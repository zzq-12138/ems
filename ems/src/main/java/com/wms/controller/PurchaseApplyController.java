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
import com.wms.entity.PurchaseApply;
import com.wms.enums.EquipmentStatusEnum;
import com.wms.enums.EquipmentTypeEnum;
import com.wms.enums.PurchaseStatusEnum;
import com.wms.service.EquipmentBaseService;
import com.wms.service.PurchaseApplyService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ems
 * @since 2025-10-29
 */
@RestController
@RequestMapping("/purchase")
public class PurchaseApplyController {

    @Resource
    private PurchaseApplyService purchaseApplyService;

    @Resource
    private EquipmentBaseService equipmentBaseService;


    @PostMapping("/submit")
    public Result submitApply(@RequestBody PurchaseApply apply) {
        System.out.println("===== 接收到/submit请求 =====");
        System.out.println("请求数据: " + apply);

        // 设置创建时间为当前时间
        apply.setCreateDate(LocalDateTime.now());
        // 设置申请状态为待审核
        apply.setStatus(PurchaseStatusEnum.TO_BE_REVIEWED);
//        apply.setApplyReason(apply.getApplyReason());
        // 保存到数据库
        boolean isSaved = purchaseApplyService.save(apply);

        if (isSaved) {
            System.out.println("申请已成功保存到数据库，ID: " + apply.getId());
            return Result.suc(apply);
        } else {
            return Result.fail("保存申请失败");
        }

    }


    /**
     * 管理员分页查看申请列表
     */
    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query) {
        System.out.println("===== 接收到/listPage请求 =====");

        HashMap param = query.getParam();
        String name = (String) param.get("name");

        // 使用安全的枚举转换方法
        PurchaseStatusEnum status = parsePurchaseStatus(param.get("status"));
        String requesterId = (String) param.get("requesterId");

        // 使用改进的日期转换方法
        LocalDateTime createDate = parseLocalDateTime(param.get("createDate"));

        Page<PurchaseApply> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<PurchaseApply> lambdaQueryWrapper = new LambdaQueryWrapper();

        // 设备名称查询（模糊查询）
        if(StringUtils.isNotBlank(name) && !"null".equals(name)){
            lambdaQueryWrapper.like(PurchaseApply::getName, name);
        }

        // 申请状态查询（精确匹配）
        if(ObjectUtils.isNotNull(status)){
            lambdaQueryWrapper.eq(PurchaseApply::getStatus, status);
        }

        // 申请时间查询
        if(ObjectUtils.isNotNull(createDate)){
            // 查询指定日期当天的记录（从00:00:00到23:59:59）
            LocalDateTime startDate = createDate.toLocalDate().atStartOfDay();
            LocalDateTime endDate = createDate.toLocalDate().atTime(23, 59, 59);
            lambdaQueryWrapper.between(PurchaseApply::getCreateDate, startDate, endDate);
        }


        // 按创建时间倒序排列
        lambdaQueryWrapper.orderByDesc(PurchaseApply::getCreateDate);

        IPage<PurchaseApply> result = purchaseApplyService.page(page, lambdaQueryWrapper);

        System.out.println("查询结果: " + result.getRecords().size() + " 条记录");
        return Result.suc(result.getRecords(), result.getTotal());

        }

    /**
     * 管理员审批
     */
    @PostMapping("/review")
    @Transactional(rollbackFor = Exception.class)
    public Result reviewApply(@RequestBody PurchaseApply review) {
        System.out.println("===== 接收到/review请求 =====");
        System.out.println("请求数据: " + review);

        // 根据ID查找现有记录
        PurchaseApply existingApply = purchaseApplyService.getById(review.getId());
        if (existingApply == null) {
            return Result.fail("未找到对应的申请记录");
        }

        // 更新现有记录的字段
        existingApply.setStatus(review.getStatus());
        existingApply.setPurchaseName(review.getPurchaseName());
        existingApply.setPurchaseId(review.getPurchaseId());
        existingApply.setPurchaseDate(LocalDateTime.now());
        existingApply.setCompany(review.getCompany());
        existingApply.setUnitPrice(review.getUnitPrice());
        existingApply.setDes(review.getDes());
        // 保存更新后的记录
        boolean isSaved = purchaseApplyService.updateById(existingApply);
        if (isSaved) {
            System.out.println("申请已成功保存到数据库，ID: " + review.getId());
        } else {
            return Result.fail("保存申请失败");
        }

        if(review.getStatus() == PurchaseStatusEnum.PASS) {

            createEquipmentRecords(review);
        }
        return Result.suc(review);
    }
    /**
     * 创建设备记录
     */
    private void createEquipmentRecords(PurchaseApply apply) {
        if (apply.getCount() == null || apply.getCount() <= 0) {
            System.out.println("采购数量无效，跳过创建设备记录");
            return;
        }

        // 获取该类型设备的最大序号
        int maxSequence = getMaxEquipmentSequence(apply.getType());

        List<EquipmentBase> equipmentList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // 根据采购数量创建设备记录
        for (int i = 1; i <= apply.getCount(); i++) {
            EquipmentBase equipment = new EquipmentBase();

            // 设置设备基本信息
            equipment.setName(apply.getName());
            equipment.setStatus(EquipmentStatusEnum.NORMAL); // 默认状态为NORMAL
            equipment.setType(apply.getType());
            equipment.setUpdateDate(now);


            // 生成设备编号：类型-序号（如：MOUSE-4）
            int sequence = maxSequence + i;
            String eqNumber = generateEquipmentNumber(apply.getType(), sequence);
            equipment.setEqNumber(eqNumber);

            // 填充厂家和购买者信息（如果在审批时提供）
            if (apply.getCompany() != null) {
                equipment.setManufacturer(apply.getCompany());
            }
            if (apply.getPurchaseId() != null) {
                equipment.setCreatorId(Long.valueOf(apply.getPurchaseId()));
            }
            if (apply.getPurchaseName() != null) {
                equipment.setCreatorName(apply.getPurchaseName());
            }

            equipmentList.add(equipment);
        }

        // 批量保存设备记录
        boolean saved = equipmentBaseService.saveBatch(equipmentList);
        if (saved) {
            System.out.println("成功创建 " + equipmentList.size() + " 条设备记录");
        } else {
            throw new RuntimeException("创建设备记录失败");
        }
    }

    /**
     * 获取指定设备类型的最大序号
     */
    private int getMaxEquipmentSequence(EquipmentTypeEnum equipmentType) {
        QueryWrapper<EquipmentBase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", equipmentType.name());
        queryWrapper.orderByDesc("eq_number");
        queryWrapper.last("LIMIT 1");

        EquipmentBase lastEquipment = equipmentBaseService.getOne(queryWrapper);
        if (lastEquipment == null || lastEquipment.getEqNumber() == null) {
            return 0;
        }

        // 从设备编号中提取序号（如从"MOUSE-1"中提取1）
        String eqNumber = lastEquipment.getEqNumber();
        try {
            String[] parts = eqNumber.split("-");
            if (parts.length >= 2) {
                return Integer.parseInt(parts[1]);
            }
        } catch (NumberFormatException e) {
            System.err.println("解析设备号失败: " + eqNumber);
        }

        return 0;
    }

    /**
     * 生成设备编号
     */
    private String generateEquipmentNumber(EquipmentTypeEnum equipmentType, int sequence) {
        return equipmentType.name() + "-" + sequence;
    }

    /**
     * 安全转换采购状态枚举
     */
    public PurchaseStatusEnum parsePurchaseStatus(Object statusObj) {
        if (statusObj == null) {
            return null;
        }

        String statusStr;
        if (statusObj instanceof PurchaseStatusEnum) {
            return (PurchaseStatusEnum) statusObj;
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
            return PurchaseStatusEnum.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            // 记录日志或返回null，根据业务需求决定
            System.err.println("无效的采购状态: " + normalized);
            return null;
        }
    }

    /**
     * 安全转换日期时间 - 增强版（支持多种格式）
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

            // 支持多种日期格式
            String[] patterns = {
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd",
                    "yyyy/MM/dd HH:mm:ss",
                    "yyyy/MM/dd"
            };

            for (String pattern : patterns) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                    // 如果只有日期部分，转换为当天的开始时间
                    if (pattern.equals("yyyy-MM-dd") || pattern.equals("yyyy/MM/dd")) {
                        return LocalDate.parse(dateStr, formatter).atStartOfDay();
                    }
                    return LocalDateTime.parse(dateStr, formatter);
                } catch (Exception e) {
                    // 继续尝试下一种格式
                    continue;
                }
            }

            System.err.println("日期格式解析失败: " + dateStr);
            return null;
        }

        return null;
    }
}

