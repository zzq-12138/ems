package com.wms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.entity.Broken;
import com.wms.mapper.BrokenMapper;
import com.wms.service.BrokenService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ems
 * @since 2025-10-29
 */
@Service
public class BrokenServiceImpl extends ServiceImpl<BrokenMapper, Broken> implements BrokenService {

    @Resource
    BrokenMapper brokenMapper;

    @Override
    public Broken view(Integer id) {
        return brokenMapper.selectById(id);
    }
}
