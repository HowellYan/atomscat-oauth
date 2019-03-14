package com.atomscat.modules.base.dao;

import com.atomscat.base.AtomscatBaseDao;
import com.atomscat.modules.base.entity.DepartmentHeader;

import java.util.List;

/**
 * 部门负责人数据处理层
 *
 * @author Howell Yang
 */
public interface DepartmentHeaderDao extends AtomscatBaseDao<DepartmentHeader, String> {

    /**
     * 通过部门和负责人类型获取
     *
     * @param departmentId
     * @param type
     * @return
     */
    List<DepartmentHeader> findByDepartmentIdAndType(String departmentId, Integer type);

    /**
     * 通过部门id删除
     *
     * @param departmentId
     */
    void deleteByDepartmentId(String departmentId);
}