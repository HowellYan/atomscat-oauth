package com.atomscat.modules.base.serviceimpl.mybatis;

import com.atomscat.modules.base.dao.mapper.PermissionMapper;
import com.atomscat.modules.base.entity.Permission;
import com.atomscat.modules.base.service.mybatis.IPermissionService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Howell Yang
 */
@Service
public class IPermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {

    @Autowired
    private PermissionMapper permissionMapper;
    ;

    @Override
    public List<Permission> findByUserId(String userId) {

        return permissionMapper.findByUserId(userId);
    }
}
