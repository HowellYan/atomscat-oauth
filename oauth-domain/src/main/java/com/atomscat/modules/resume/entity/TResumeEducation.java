package com.atomscat.modules.resume.entity;


import com.atomscat.base.XbootBaseEntity;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "t_resume_education")
@TableName("t_resume_education")
@ApiModel(value = "简历教育")
public class TResumeEducation extends XbootBaseEntity {

    @ApiModelProperty(value = "t_resume_basis`s id")
    private String resumeId;

    @ApiModelProperty(value = "学历/学位")
    private String degree;

    @ApiModelProperty(value = "毕业学校")
    private String school;

    @ApiModelProperty(value = "入学时间")
    private String admissionTime;

    @ApiModelProperty(value = "毕业时间")
    private String graduationTime;

    @ApiModelProperty(value = "专业")
    private String profession;
}
