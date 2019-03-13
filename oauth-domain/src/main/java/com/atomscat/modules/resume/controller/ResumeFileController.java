package com.atomscat.modules.resume.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.atomscat.common.exception.XbootException;
import com.atomscat.common.limit.RedisRaterLimiter;
import com.atomscat.common.utils.IpInfoUtil;
import com.atomscat.common.utils.PageUtil;
import com.atomscat.common.utils.ResultUtil;
import com.atomscat.common.utils.SecurityUtil;
import com.atomscat.common.vo.PageVo;
import com.atomscat.common.vo.Result;
import com.atomscat.common.vo.SearchVo;
import com.atomscat.modules.base.entity.Dict;
import com.atomscat.modules.base.entity.DictData;
import com.atomscat.modules.base.entity.User;
import com.atomscat.modules.base.service.DictDataService;
import com.atomscat.modules.base.service.DictService;
import com.atomscat.modules.resume.entity.ExportFileResp;
import com.atomscat.modules.resume.entity.ImportFileResp;
import com.atomscat.modules.resume.entity.ResumeList;
import com.atomscat.modules.resume.entity.TResumeAnnex;
import com.atomscat.modules.resume.service.ResumeBaseService;
import com.atomscat.modules.resume.service.ResumeFileService;
import com.baomidou.mybatisplus.plugins.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Api(description = "简历文件处理接口")
@RequestMapping("/rmp/resume")
@Transactional
public class ResumeFileController {

    @Autowired
    private IpInfoUtil ipInfoUtil;

    @Autowired
    private RedisRaterLimiter redisRaterLimiter;

    @Autowired
    private ResumeBaseService resumeBaseService;

    @Autowired
    private ResumeFileService resumeFileService;

    @Autowired
    private DictService dictService;

    @Autowired
    private DictDataService dictDataService;

    @Autowired
    private SecurityUtil securityUtil;

    private String classpath = "/data/file/";

    @RequestMapping(value = "/fileLocal", method = RequestMethod.POST)
    @ApiOperation(value = "简历数据导入")
    public Result<Object> uploadToLocal(@RequestParam("file") MultipartFile multipartFile,
                                        HttpServletRequest request, HttpServletResponse httpServletResponse) {

        // IP限流 在线Demo所需 1秒限1个请求
        String token = redisRaterLimiter.acquireTokenFromBucket("upload:" + ipInfoUtil.getIpAddr(request), 1, 1000);
        if (StrUtil.isBlank(token)) {
            throw new XbootException("上传那么多干嘛，等等再传吧");
        }
        String result = null;
        try {
            ExcelReader reader = ExcelUtil.getReader(multipartFile.getInputStream(), 0);
            List<Map<String, Object>> readAll = reader.readAll();
            User u = securityUtil.getCurrUser();
            ImportFileResp importFileResp = resumeBaseService.importFile(readAll, u);

            if (importFileResp.getIsRepeat()) {
                String id = UUID.fastUUID().toString().replace("-", "");
                ExcelWriter writer = reader.getWriter();
                writer.write(importFileResp.getList());
                String fileName = classpath + DateUtil.format(new Date(), "yyyyMMddHHmmss") + "_" + multipartFile.getOriginalFilename();
                File file = new File(fileName);
                writer.flush(file);
                writer.close();
                reader.close();
                return new ResultUtil<Object>().setErrorMsg(101, "数据有重复", fileName);
            }
            reader.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return new ResultUtil<Object>().setData(result);
    }

    @RequestMapping(value = "/updateTimeLocal", method = RequestMethod.POST)
    @ApiOperation(value = "简历更新出生日期数据")
    public Result<Object> updateTime(@RequestParam("file") MultipartFile multipartFile) {
        String result = null;
        try {
            ExcelReader reader = ExcelUtil.getReader(multipartFile.getInputStream(), 0);
            List<Map<String, Object>> readAll = reader.readAll();
            resumeBaseService.importFileUpdateTime(readAll);
            reader.close();
            result = "成功";
        } catch (Throwable e) {
            e.printStackTrace();
            log.error(e.getMessage());
            result = "失败";
        }
        return new ResultUtil<Object>().setData(result);
    }


    @RequestMapping(value = "/downloadFile", method = RequestMethod.GET)
    @ApiOperation(value = "下载导入出错的数据")
    public String downloadFile(@RequestParam String filePath, HttpServletResponse response) {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());// 文件名
        if (fileName != null) {
            //设置文件路径
            File file = new File(filePath);
            if (file.exists() && resumeFileService.downloadByResponse(file, fileName, response)) {
                if (file.delete()) {
                    System.out.println(file.getName() + " is deleted!");
                } else {
                    System.out.println("Delete operation is failed.");
                }
            }
        }
        return "下载失败";
    }


    @RequestMapping(value = "/uploadAnnex", method = RequestMethod.POST)
    @ApiOperation(value = "附件上传")
    public Result<Object> uploadAnnex(@RequestParam("file") MultipartFile multipartFile,
                                      HttpServletRequest request, HttpServletResponse httpServletResponse) {
        User u = securityUtil.getCurrUser();
        String result = "";
        String fileName = "";
        try {
            try {
                fileName = java.net.URLDecoder.decode(multipartFile.getOriginalFilename(), "UTF-8");
            }catch (Exception e) {
                fileName = multipartFile.getOriginalFilename();
            }
            File file = new File(classpath + fileName);
            if (file.exists()) {
                result = fileName + ",文件已经存在";
                return new ResultUtil<Object>().setErrorMsg(100, result, fileName);
            } else {
                FileUtils.writeByteArrayToFile(file, multipartFile.getBytes());
                resumeFileService.saveAnnex(fileName,classpath + fileName, 0, u);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage() + ":" + e.getLocalizedMessage());
            result = "文件处理失败";
            return new ResultUtil<Object>().setErrorMsg(500, result, multipartFile.getOriginalFilename());
        }
        return new ResultUtil<Object>().setData(result);
    }

    @RequestMapping(value = "/downloadAnnex", method = RequestMethod.GET)
    @ApiOperation(value = "下载附件")
    public String downloadAnnex(@RequestParam String name, HttpServletResponse response) {
        File file = new File(classpath);
        File[] tempFile = file.listFiles();
        List<String> stringList = new ArrayList<>();
        for (File item : tempFile) {
            if (item.getName().startsWith(name) || item.getName().endsWith(name) || item.getName().contains(name)) {
                if (item.exists()) {
                    System.out.println("文件名:" + item.getName());
                    stringList.add(classpath + item.getName());

                }
            }
        }

        if (stringList.size() == 1) {
            String fileName = stringList.get(0).substring(stringList.get(0).lastIndexOf("/") + 1, stringList.get(0).length());// 文件名
            resumeFileService.downloadByResponse(new File(stringList.get(0)), fileName, response);
        } else if (stringList.size() > 1) {// 批量文件压缩后下载
            String fileName = name +"_" + DateUtil.format(new Date(), "yyyyMMddHHmmss") +".zip";
            resumeFileService.zipFileList(stringList, classpath + fileName);

            File zapFile = new File(classpath + fileName);
            if (zapFile.exists() && resumeFileService.downloadByResponse(zapFile, fileName, response) ) {
                zapFile.delete();
                System.out.println(zapFile.getName() + " is deleted!");
            }
        }

        return "下载失败";
    }

    @RequestMapping(value = "/annexList", method = RequestMethod.POST)
    @ApiOperation(value = "附件列表")
    public Result<Page<TResumeAnnex>> getAnnexList(@ModelAttribute TResumeAnnex tResumeAnnex,
                                                   @ModelAttribute SearchVo searchVo,
                                                   @ModelAttribute PageVo pageVo) {
        User u = securityUtil.getCurrUser();
        Page<TResumeAnnex> page = resumeFileService.findPage(tResumeAnnex, searchVo, PageUtil.initPage(pageVo), u);
        return new ResultUtil<Page<TResumeAnnex>>().setData(page);
    }

    @RequestMapping(value = "/downloadResumeData", method = RequestMethod.GET)
    @ApiOperation(value = "下载简历数据")
    public String downloadResumeData(@RequestParam String[] ids,
                                     @ModelAttribute ResumeList resumeList,
                                     @ModelAttribute SearchVo searchVo,
                                     @ModelAttribute PageVo pageVo, HttpServletResponse response) throws IOException {
        User u = securityUtil.getCurrUser();
        if (ids != null && ids.length > 0) {
            pageVo.setPageNumber(1);
            pageVo.setPageSize(ids.length);
        } else {
            pageVo.setPageNumber(1);
            pageVo.setPageSize(10);
        }

        List<DictData> customer_level = getByType("customer_level");
        List<DictData> product_list = getByType("product_list");

        org.springframework.data.domain.Page<ResumeList> page = resumeBaseService.findPage(resumeList, searchVo, PageUtil.initPage(pageVo), u, ids);

        // 通过工具类创建writer，默认创建xls格式
        ExcelWriter writer = ExcelUtil.getBigWriter();
        writer.setHeaderAlias(ExportFileResp.getResumeHeader());

        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> list = new ArrayList<>();
        page.getContent().forEach((item) -> {
            list.add(translate(objectMapper.convertValue(item, Map.class), customer_level, product_list));
        });

        int totalPages = page.getTotalPages();
        for (int i = 1; i < totalPages; i++) {
            pageVo.setPageNumber(i + 1);
            page = resumeBaseService.findPage(resumeList, searchVo, PageUtil.initPage(pageVo), u, ids);
            page.getContent().forEach((item) -> {
                list.add(translate(objectMapper.convertValue(item, Map.class), customer_level, product_list));
            });
        }
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(list, true);

        //response为HttpServletResponse对象
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=resume_list_" + new Date() + ".xls");

        //out为OutputStream，需要写出到的目标流
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
        return "下载失败";
    }

    /**
     * 下载简历数据 数据转换
     *
     * @param map
     * @param customer_level
     * @param product_list
     * @return
     */
    private Map<String, Object> translate(Map<String, Object> map, List<DictData> customer_level, List<DictData> product_list) {
        map.remove("id");
        map.remove("_canFollow");
        map.remove("createBy");
        map.remove("createTime");
        map.remove("delFlag");
        map.remove("endNum");
        map.remove("startNum");
        map.remove("updateBy");
        map.remove("updateTime");
        customer_level.forEach((item) -> {
            if (String.valueOf(map.get("level")).equals(item.getValue())) {
                map.put("level", item.getTitle());
            }
        });
        product_list.forEach((item) -> {
            if (String.valueOf(map.get("product")).equals(item.getValue())) {
                map.put("product", item.getTitle());
            }
        });
        return map;
    }

    /**
     * 获取字典数据
     *
     * @param type
     * @return
     */
    public List<DictData> getByType(String type) {
        Dict dict = dictService.findByType(type);
        if (dict == null) {
            return null;
        }
        List<DictData> list = dictDataService.findByDictId(dict.getId());
        return list;
    }

}
