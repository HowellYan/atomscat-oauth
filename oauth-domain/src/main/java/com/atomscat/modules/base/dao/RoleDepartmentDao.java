package com.atomscat.modules.base.dao;

import com.atomscat.base.AtomscatBaseDao;
import com.atomscat.modules.base.entity.RoleDepartment;

import java.util.List;

/**
 * 角色部门数据处理层
 * @author Howell Yang
 */
public interface RoleDepartmentDao extends AtomscatBaseDao<RoleDepartment,String> {

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