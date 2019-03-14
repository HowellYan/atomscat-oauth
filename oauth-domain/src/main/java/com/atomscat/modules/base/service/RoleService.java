package com.atomscat.modules.base.service;


import com.atomscat.base.AtomscatBaseService;
import com.atomscat.modules.base.entity.Role;

import java.util.List;

/**
 * 角色接口
 *
 * @author Howell Yang
 */
public interface RoleService extends AtomscatBaseService<Role, String> {

    /**
     * 获取默认角色
     *
     * @param defaultRole
     * @return
     */
    List<Role> findByDefaultRole(Boolean defaultRole);
}
