package com.atomscat.modules.resume.service;

import com.atomscat.base.XbootBaseService;
import com.atomscat.common.vo.SearchVo;
import com.atomscat.modules.base.entity.User;
import com.atomscat.modules.resume.entity.ImportFileResp;
import com.atomscat.modules.resume.entity.ResumeList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 简历基础
 */
public interface ResumeBaseService extends XbootBaseService<ResumeList, String> {

    /**
     * 多条件分页获取简历基础
     * @param resumeList
     * @param searchVo
     * @param pageable
     * @param u
     * @param ids 导出选择的数据id
     * @return
     */
    Page<ResumeList> findPage(ResumeList resumeList, SearchVo searchVo, Pageable pageable, User u, String[] ids);

    /**
     * 默认展开当前行
     *
     * @param expanded
     * @return
     */
    boolean expanded(boolean expanded, String id);

    /**
     * 修改简历跟进人
     *
     * @param ids
     * @param systemUserId
     * @return
     */
    boolean modifyFollowers(String[] ids, String systemUserId, User u);

    /**
     * xls 导入
     * @param mapList
     * @return
     */
    ImportFileResp importFile(List<Map<String, Object>> mapList, User u);


    void importFileUpdateTime(List<Map<String, Object>> mapList);
}
