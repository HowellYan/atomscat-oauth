package com.atomscat.modules.base.dao;

import com.atomscat.base.XbootBaseDao;
import com.atomscat.modules.base.entity.RolePermission;

import java.util.List;

/**
 * 角色权限数据处理层
 * @author Howell Yang
 */
public interface RolePermissionDao extends XbootBaseDao<RolePermission,String> {

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