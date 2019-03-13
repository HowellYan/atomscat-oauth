package com.atomscat.modules.resume.serviceimpl;

import cn.hutool.core.lang.UUID;
import com.atomscat.common.vo.SearchVo;
import com.atomscat.modules.base.entity.User;
import com.atomscat.modules.resume.dao.mapper.TResumeAnnexMapper;
import com.atomscat.modules.resume.entity.TResumeAnnex;
import com.atomscat.modules.resume.service.ResumeFileService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;


@Slf4j
@Service
@Transactional
public class ResumeFileServiceImpl implements ResumeFileService {

    @Resource
    private TResumeAnnexMapper tResumeAnnexMapper;

    /**
     * 附件上传
     *
     * @param fileName
     * @param filePath
     * @param fileType
     * @param u
     * @return
     */
    public boolean saveAnnex(String fileName, String filePath, int fileType, User u) {
        TResumeAnnex tResumeAnnex = new TResumeAnnex();
        String id = UUID.fastUUID().toString().replace("-", "");
        tResumeAnnex.setId(id);
        tResumeAnnex.setFileName(fileName);
        tResumeAnnex.setFilePath(filePath);
        tResumeAnnex.setFileType(fileType);
        tResumeAnnex.setCreateBy(u.getId());
        tResumeAnnex.setCreateTime(new Date());
        if (tResumeAnnexMapper.insert(tResumeAnnex) > 0) {
            return true;
        }
        return false;
    }

    /**
     * 附件列表
     *
     * @param tResumeAnnex
     * @param searchVo
     * @param pageable
     * @param u
     * @return
     */
    public Page<TResumeAnnex> findPage(TResumeAnnex tResumeAnnex, SearchVo searchVo, Pageable pageable, User u) {
        EntityWrapper<TResumeAnnex> tResumeAnnexEntityWrapper = new EntityWrapper<>();

        if (!StringUtils.isEmpty(tResumeAnnex.getFileName())) {
            tResumeAnnexEntityWrapper.like("file_name", tResumeAnnex.getFileName());
        }
        Page<TResumeAnnex> page = new Page<>(pageable.getPageNumber() + 1, tResumeAnnexMapper.selectCount(tResumeAnnexEntityWrapper));
        page.setSize(pageable.getPageSize());
        List<TResumeAnnex> listPage = tResumeAnnexMapper.selectPage(page, tResumeAnnexEntityWrapper);
        page.setRecords(listPage);
        return page;
    }


    /**
     * 批量文件压缩
     * @param list
     * @param zipFilePath
     */
    public void zipFileList(List<String> list, String zipFilePath) {

        try {
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFilePath));
            InputStream input = null;
            for (String filePath : list) {
                input = new FileInputStream(new File(filePath));
                String fileName = filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());// 文件名
                zipOut.putNextEntry(new ZipEntry(java.net.URLEncoder.encode(fileName, "gbk")));
                byte[] b = new byte[100];
                int length = 0;
                while((length = input.read(b))!= -1){
                    zipOut.write(b, 0, length);
                }
                input.close();
            }
            zipOut.setEncoding("gbk");
            zipOut.closeEntry();
            zipOut.close();
        } catch (Exception e) {

        }

    }

    /**
     * 流下载
     *
     * @param file
     * @param fileName
     * @param response
     * @return
     */
    public boolean downloadByResponse(File file, String fileName, HttpServletResponse response) {
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode(fileName, "UTF-8"));// 设置文件名
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
