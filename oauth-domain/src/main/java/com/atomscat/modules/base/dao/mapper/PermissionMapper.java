package com.atomscat.modules.base.dao.mapper;

import com.atomscat.modules.base.entity.Permission;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Howell Yang
 */
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 通过用户id获取
     *
     * @param userId
     * @return
     */
    List<Permission> findByUserId(@Param("userId") String userId);
}
