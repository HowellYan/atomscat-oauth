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
@Table(name = "t_resume_post")
@TableName("t_resume_post")
@ApiModel(value = "简历岗位")
public class TResumePost extends XbootBaseEntity {

    @ApiModelProperty(value = "t_resume_basis`s id")
    private String resumeId;

    @ApiModelProperty(value = "入职时间")
    private String entryTime;

    @ApiModelProperty(value = "离职时间")
    private String separationTime;

    @ApiModelProperty(value = "公司名称")
    private String company;

    @ApiModelProperty(value = "职位")
    private String position;

    @ApiModelProperty(value = "最高年收入")
    private String annualIncome;

}
