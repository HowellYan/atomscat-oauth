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
@Table(name = "t_resume_annex")
@TableName("t_resume_annex")
@ApiModel(value = "简历附件")
public class TResumeAnnex  extends XbootBaseEntity{

  @ApiModelProperty(value = "t_resume_basis`s id")
  private String resumeId;

  @ApiModelProperty(value = "文件名称")
  private String fileName;

  @ApiModelProperty(value = "文件路径")
  private String filePath;

  @ApiModelProperty(value = "文件格式")
  private String fileFormat;

  @ApiModelProperty(value = "文件存储类型")
  private Integer fileType;
}
