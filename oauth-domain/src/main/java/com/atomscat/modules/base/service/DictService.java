package com.atomscat.modules.base.service;

import com.atomscat.base.AtomscatBaseService;
import com.atomscat.modules.base.entity.Dict;

import java.util.List;

/**
 * 字典接口
 *
 * @author Howell Yang
 */
public interface DictService extends AtomscatBaseService<Dict, String> {

    /**
     * 排序获取全部
     *
     * @return
     */
    List<Dict> findAllOrderBySortOrder();

    /**
     * 通过type获取
     *
     * @param type
     * @return
     */
    Dict findByType(String type);

    /**
     * 模糊搜索
     *
     * @param key
     * @return
     */
    List<Dict> findByTitleOrTypeLike(String key);
}