package com.atomscat.modules.resume.entity;


import com.atomscat.base.XbootBaseEntity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Data
@Entity
@Table(name = "t_resume_follow_up")
@TableName("t_resume_follow_up")
@ApiModel(value = "简历跟进")
public class TResumeFollowUp extends XbootBaseEntity {

    @ApiModelProperty(value = "t_resume_basis`s id")
    private String resumeId;

    @ApiModelProperty(value = "跟进人 id")
    private String systemUserId;

    @ApiModelProperty(value = "跟进人 name")
    @Transient
    @TableField(exist=false)
    private String systemUserName;

    @ApiModelProperty(value = "状态")
    private Integer followStatus;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "客户级别")
    private Integer level;

    @ApiModelProperty(value = "下次跟进")
    private String nextFollow;

    @ApiModelProperty(value = "意向产品")
    private String product;
}
