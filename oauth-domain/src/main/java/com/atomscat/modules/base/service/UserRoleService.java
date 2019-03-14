package com.atomscat.modules.base.service;


import com.atomscat.base.AtomscatBaseService;
import com.atomscat.modules.base.entity.UserRole;

import java.util.List;

/**
 * 用户角色接口
 *
 * @author Howell Yang
 */
public interface UserRoleService extends AtomscatBaseService<UserRole, String> {

    /**
     * 通过roleId查找
     *
     * @param roleId
     * @return
     */
    List<UserRole> findByRoleId(String roleId);

    /**
     * 删除用户角色
     *
     * @param userId
     */
    void deleteByUserId(String userId);
}
