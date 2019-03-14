package com.atomscat.modules.base.service;

import com.atomscat.base.AtomscatBaseService;
import com.atomscat.modules.base.entity.DepartmentHeader;

import java.util.List;

/**
 * 部门负责人接口
 *
 * @author Howell Yang
 */
public interface DepartmentHeaderService extends AtomscatBaseService<DepartmentHeader, String> {

    /**
     * 通过部门和负责人类型获取
     *
     * @param departmentId
     * @param type
     * @return
     */
    List<String> findHeaderByDepartmentId(String departmentId, Integer type);

    /**
     * 通过部门id删除
     *
     * @param departmentId
     */
    void deleteByDepartmentId(String departmentId);
}