package com.atomscat.modules.base.service;

import com.atomscat.base.XbootBaseService;
import com.atomscat.modules.base.entity.RolePermission;

import java.util.List;

/**
 * 角色权限接口
 * @author Howell Yang
 */
public interface RolePermissionService extends XbootBaseService<RolePermission,String> {

    /**
     * 通过permissionId获取
     * @param permissionId
     * @return
     */
    List<RolePermission> findByPermissionId(String permissionId);

    /**
     * 通过roleId获取
     * @param roleId
     */
    List<RolePermission> findByRoleId(String roleId);

    /**
     * 通过roleId删除
     * @param roleId
     */
    void deleteByRoleId(String roleId);
}