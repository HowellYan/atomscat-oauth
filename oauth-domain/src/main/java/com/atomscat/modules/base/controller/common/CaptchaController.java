package com.atomscat.modules.base.controller.common;

import com.atomscat.common.utils.CreateVerifyCode;
import com.atomscat.common.utils.ResultUtil;
import com.atomscat.common.vo.Captcha;
import com.atomscat.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Howell Yang
 */
@Api(description = "验证码接口")
@RequestMapping("/rmp/common/captcha")
@RestController
@Transactional
public class CaptchaController {


    @RequestMapping(value = "/init", method = RequestMethod.GET)
    @ApiOperation(value = "初始化验证码")
    public Result<Object> initCaptcha() {

        String captchaId = UUID.randomUUID().toString().replace("-", "");
        String code = new CreateVerifyCode().randomStr(4);
        Captcha captcha = new Captcha();
        captcha.setCaptchaId(captchaId);

        return new ResultUtil<Object>().setData(captcha);
    }

    @RequestMapping(value = "/draw/{captchaId}", method = RequestMethod.GET)
    @ApiOperation(value = "根据验证码ID获取图片")
    public void drawCaptcha(@PathVariable("captchaId") String captchaId, HttpServletResponse response) throws IOException {


        CreateVerifyCode vCode = new CreateVerifyCode(116, 36, 4, 10);
        vCode.write(response.getOutputStream());
    }
}
