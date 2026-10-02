package com.wms.utils;

import com.wms.enums.AuditStatusEnum;
import com.wms.enums.AuditTypeEnum;
import com.wms.enums.EquipmentStatusEnum;
import com.wms.enums.EquipmentTypeEnum;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TransformUtil {
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
            case "PC":
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

    public AuditStatusEnum parseAuditStatus(Object status) {

        if (status == null) {
            return null;
        }

        String statusStr;
        if (status instanceof AuditStatusEnum) {
            return (AuditStatusEnum) status;
        } else if (status instanceof String) {
            statusStr = (String) status;
        } else {
            statusStr = status.toString();
        }

        if (statusStr.trim().isEmpty() || "null".equalsIgnoreCase(statusStr)) {
            return null;
        }

        // 统一转换为大写
        String normalized = statusStr.toUpperCase().trim();

        try {
            return AuditStatusEnum.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            // 记录日志或返回null，根据业务需求决定
            return null;
        }
    }

    public AuditTypeEnum parseAuditTypeEnum(Object auditType) {

        if (auditType == null) {
            return null;
        }

        String statusStr;
        if (auditType instanceof AuditTypeEnum) {
            return (AuditTypeEnum) auditType;
        } else if (auditType instanceof String) {
            statusStr = (String) auditType;
        } else {
            statusStr = auditType.toString();
        }

        if (statusStr.trim().isEmpty() || "null".equalsIgnoreCase(statusStr)) {
            return null;
        }

        // 统一转换为大写
        String normalized = statusStr.toUpperCase().trim();

        try {
            return AuditTypeEnum.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            // 记录日志或返回null，根据业务需求决定
            return null;
        }
    }
}
