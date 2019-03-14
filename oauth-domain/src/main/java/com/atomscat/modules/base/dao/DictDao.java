package com.atomscat.modules.base.dao;

import com.atomscat.base.AtomscatBaseDao;
import com.atomscat.modules.base.entity.Dict;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 字典数据处理层
 *
 * @author Howell Yang
 */
public interface DictDao extends AtomscatBaseDao<Dict, String> {

    /**
     * 排序获取全部
     *
     * @return
     */
    @Query(value = "select * from t_dict d order by d.sort_order", nativeQuery = true)
    List<Dict> findAllOrderBySortOrder();

    /**
     * 通过type获取
     *
     * @param type
     * @return
     */
    List<Dict> findByType(String type);

    /**
     * 模糊搜索
     *
     * @param key
     * @return
     */
    @Query(value = "select * from t_dict d where d.title like %:key% or d.type like %:key% order by d.sort_order", nativeQuery = true)
    List<Dict> findByTitleOrTypeLike(@Param("key") String key);
}