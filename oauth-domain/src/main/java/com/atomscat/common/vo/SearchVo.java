package com.atomscat.common.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Howell Yang
 */
@Data
public class SearchVo implements Serializable {

    private String startDate;

    private String endDate;
}
