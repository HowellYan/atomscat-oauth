package com.atomscat.modules.base.service.mybatis;

import com.atomscat.modules.base.entity.Permission;
import com.baomidou.mybatisplus.service.IService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * @author Howell Yang
 */
@CacheConfig(cacheNames = "userPermission")
public interface IPermissionService extends IService<Permission> {

    /**
     * 通过用户id获取
     * @param userId
     * @return
     */
    @Cacheable(key = "#userId")
    List<Permission> findByUserId(String userId);
}
