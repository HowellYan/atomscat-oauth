package com.atomscat.modules.base.controller.common;


import com.atomscat.common.utils.IpInfoUtil;
import com.atomscat.common.utils.ResultUtil;
import com.atomscat.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;

/**
 * @author Howell Yang
 */
@Slf4j
@RestController
@Api(description = "文件上传接口")
@RequestMapping("/rmp/upload")
@Transactional
public class UploadController {


    @Autowired
    private IpInfoUtil ipInfoUtil;

    @RequestMapping(value = "/file", method = RequestMethod.POST)
    @ApiOperation(value = "文件上传")
    public Result<Object> upload(@RequestParam("file") MultipartFile file,
                                 HttpServletRequest request) {


        String result = null;
        // todo: 阿里云OSS
        String fileName = "";
        try {
            FileInputStream inputStream = (FileInputStream) file.getInputStream();
            // todo: 阿里云OSS
            result = "";
        } catch (Exception e) {
            log.error(e.toString());
            return new ResultUtil<Object>().setErrorMsg(e.toString());
        }

        return new ResultUtil<Object>().setData(result);
    }

    @RequestMapping(value = "/fileLocal", method = RequestMethod.POST)
    @ApiOperation(value = "文件上传")
    public Result<Object> uploadToLocal(@RequestParam("file") MultipartFile multipartFile,
                                        HttpServletRequest request) {


        String result = null;

        String classpath = this.getClass().getResource("/").getPath();
        String fileName = classpath + "/" + multipartFile.getOriginalFilename();
        try {
            File file = new File(fileName);
            FileUtils.writeByteArrayToFile(file, multipartFile.getBytes());

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return new ResultUtil<Object>().setData(result);
    }

}
