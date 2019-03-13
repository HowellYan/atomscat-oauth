package com.atomscat.common.exception;

import lombok.Data;

/**
 * @author Howell Yang
 */
@Data
public class XbootException extends RuntimeException {

    private String msg;

    public XbootException(String msg){
        super(msg);
        this.msg = msg;
    }
}
