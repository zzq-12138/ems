package com.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.entity.Broken;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ems
 * @since 2025-10-29
 */
public interface BrokenService extends IService<Broken> {

    Broken view(Integer id);
}
