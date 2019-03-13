package com.atomscat.common.exception;

import lombok.Data;

/**
 * @author Howell Yang
 */
@Data
public class AtomscatException extends RuntimeException {

    private String msg;

    public AtomscatException(String msg){
        super(msg);
        this.msg = msg;
    }
}
