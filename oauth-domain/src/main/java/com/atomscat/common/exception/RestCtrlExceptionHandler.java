package com.atomscat.common.exception;

import com.atomscat.common.utils.ResultUtil;
import com.atomscat.common.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Howell Yang
 */
@Slf4j
@RestControllerAdvice
public class RestCtrlExceptionHandler {

    @ExceptionHandler(AtomscatException.class)
    @ResponseStatus(value = HttpStatus.OK)
    public Result<Object> handleXCloudException(AtomscatException e) {

        String errorMsg = "Atomscat exception";
        if (e != null) {
            errorMsg = e.getMsg();
            log.error(e.toString());
        }
        return new ResultUtil<>().setErrorMsg(500, errorMsg);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.OK)
    public Result<Object> handleException(Exception e) {

        String errorMsg = "Exception";
        if (e != null) {
            errorMsg = e.getMessage();
            log.error(e.toString());
        }
        return new ResultUtil<>().setErrorMsg(500, errorMsg);
    }
}
