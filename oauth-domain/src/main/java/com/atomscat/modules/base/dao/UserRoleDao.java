package com.atomscat.modules.base.dao;

import com.atomscat.base.AtomscatBaseDao;
import com.atomscat.modules.base.entity.UserRole;

import java.util.List;

/**
 * 用户角色数据处理层
 * @author Howell Yang
 */
public interface UserRoleDao extends AtomscatBaseDao<UserRole,String> {

    /**
     * 通过roleId查找
     * @param roleId
     * @return
     */
    List<UserRole> findByRoleId(String roleId);

    /**
     * 删除用户角色
     * @param userId
     */
    void deleteByUserId(String userId);
}
