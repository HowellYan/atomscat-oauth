package com.atomscat.modules.base.service;


import com.atomscat.base.AtomscatBaseService;
import com.atomscat.common.vo.SearchVo;
import com.atomscat.modules.base.entity.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 日志接口
 *
 * @author Howell Yang
 */
public interface LogService extends AtomscatBaseService<Log, String> {

    /**
     * 分页搜索获取日志
     *
     * @param type
     * @param key
     * @param searchVo
     * @param pageable
     * @return
     */
    Page<Log> findByConfition(Integer type, String key, SearchVo searchVo, Pageable pageable);

    /**
     * 删除所有
     */
    void deleteAll();
}
