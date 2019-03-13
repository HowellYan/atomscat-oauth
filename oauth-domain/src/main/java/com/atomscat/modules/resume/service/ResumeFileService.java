package com.atomscat.modules.resume.service;

import com.atomscat.common.vo.SearchVo;
import com.atomscat.modules.base.entity.User;
import com.atomscat.modules.resume.entity.TResumeAnnex;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

public interface ResumeFileService {
    /**
     * 附件上传
     * @param fileName
     * @param filePath
     * @param fileType
     * @param u
     * @return
     */
    boolean saveAnnex(String fileName, String filePath, int fileType, User u);


    /**
     * 附件列表
     * @param tResumeAnnex
     * @param searchVo
     * @param pageable
     * @param u
     * @return
     */
    Page<TResumeAnnex> findPage(TResumeAnnex tResumeAnnex, SearchVo searchVo, Pageable pageable, User u);

    /**
     * 流下载
     * @param file
     * @param fileName
     * @param response
     * @return
     */
    boolean downloadByResponse(File file, String fileName, HttpServletResponse response);


    /**
     * 批量文件压缩
     * @param list
     * @param zipFilePath
     */
    void zipFileList(List<String> list, String zipFilePath);
}
