package com.atomscat.modules.resume.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 简历数据导入，错误提醒
 */
@Data
public class ImportFileResp implements Serializable {

    /**
     * xls 数据
     */
    List<Map<String,Object>> list;

    /**
     * 是否存在错误
     */
    Boolean isRepeat;
}
