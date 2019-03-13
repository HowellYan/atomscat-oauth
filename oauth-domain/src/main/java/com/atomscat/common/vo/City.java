package com.atomscat.common.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Howell Yang
 */
@Data
public class City implements Serializable {

    String country;

    String province;

    String city;
}
