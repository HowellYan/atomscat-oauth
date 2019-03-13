package com.atomscat.modules.base.service;

import com.atomscat.base.AtomscatBaseService;
import com.atomscat.modules.base.entity.RoleDepartment;

import java.util.List;

/**
 * 角色部门接口
 * @author Howell Yang
 */
public interface RoleDepartmentService extends AtomscatBaseService<RoleDepartment,String> {

    /**
     * 通过roleId获取
     * @param roleId
     * @return
     */
    List<RoleDepartment> findByRoleId(String roleId);

    /**
     * 通过角色id删除
     * @param roleId
     */
    void deleteByRoleId(String roleId);

    /**
     * 通过角色id删除
     * @param departmentId
     */
    void deleteByDepartmentId(String departmentId);
}