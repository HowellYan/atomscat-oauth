package com.atomscat.modules.resume.entity;

import java.util.HashMap;
import java.util.Map;

public class ExportFileResp {
    public static Map<String, String> getResumeHeader(){
        Map<String, String> map = new HashMap<>();
        map.put("name", "姓名");
        map.put("sex", "性别");
        map.put("dateOfBirth","出生日期");
        map.put("currentResidence","目前居住地");
        map.put("address","地址");
        map.put("zipCode","邮编");
        map.put("number","简历编号");
        map.put("applyPosition","应聘职位");
        map.put("applyCompany","应聘公司");
        map.put("publishCity","发布城市");
        map.put("applyTime","应聘日期");
        map.put("workingYears","工龄");
        map.put("contactNumber","联系电话");
        map.put("email","电子邮件");
        map.put("expectedSalary","期望薪资");
        map.put("jobSearchingStatus","求职状态");
        map.put("systemUserId","跟进人id");
        map.put("followStatus","状态");
        map.put("level","客户级别");
        map.put("product","意向产品");
        map.put("dataSources","数据来源");
        map.put("_expanded","默认展开当前行");
        map.put("systemUserName","跟进人");
        map.put("tresumeEducationList","简历教育");
        map.put("tresumePostList","简历岗位");
        map.put("tresumeFollowUpList","简历跟进");
        return map;
    }
}
