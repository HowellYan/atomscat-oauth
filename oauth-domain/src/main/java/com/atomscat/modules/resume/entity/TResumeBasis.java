package com.atomscat.modules.resume.entity;

import com.atomscat.base.XbootBaseEntity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "t_resume_basis")
@TableName("t_resume_basis")
@ApiModel(value = "简历基础")
public class TResumeBasis extends XbootBaseEntity {

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "性别")
    private Integer sex;

    @ApiModelProperty(value = "出生日期")
    private Date dateOfBirth;

    @ApiModelProperty(value = "目前居住地")
    private String currentResidence;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "邮编")
    private String zipCode;

    @ApiModelProperty(value = "简历编号")
    private String number;

    @ApiModelProperty(value = "应聘职位")
    private String applyPosition;

    @ApiModelProperty(value = "应聘公司")
    private String applyCompany;

    @ApiModelProperty(value = "发布城市")
    private String publishCity;

    @ApiModelProperty(value = "应聘日期")
    private String applyTime;

    @ApiModelProperty(value = "工龄")
    private Integer workingYears;

    @ApiModelProperty(value = "联系电话")
    private String contactNumber;

    @ApiModelProperty(value = "电子邮件")
    private String eMail;

    @ApiModelProperty(value = "期望薪资")
    private String expectedSalary;

    @ApiModelProperty(value = "求职状态")
    private Integer jobSearchingStatus;

    @ApiModelProperty(value = "跟进人 id")
    private String systemUserId;

    @ApiModelProperty(value = "状态")
    private Integer followStatus;

    @ApiModelProperty(value = "客户级别")
    private Integer level;

    @ApiModelProperty(value = "意向产品")
    private String product;

    @ApiModelProperty(value = "默认展开当前行")
    private boolean _expanded;

    @ApiModelProperty(value = "数据来源")
    private String dataSources;

    @ApiModelProperty(value = "下次跟进时间")
    @TableField(strategy = FieldStrategy.IGNORED)
    private Date nextFollow;

    @ApiModelProperty(value = "下次跟进时间")
    @TableField(strategy = FieldStrategy.IGNORED)
    private Date nextFollowEnd;

    @ApiModelProperty(value = "最高年收入")
    private String annualIncome;

    @ApiModelProperty(value = "学历/学位")
    private String degree;
}
