package com.atomscat.common.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Howell Yang
 */
@Data
public class IpLocate implements Serializable {

    private String retCode;

    private City result;
}

