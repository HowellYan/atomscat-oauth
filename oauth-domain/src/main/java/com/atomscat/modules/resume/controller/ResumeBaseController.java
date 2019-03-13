package com.atomscat.modules.resume.controller;

import com.atomscat.common.utils.PageUtil;
import com.atomscat.common.utils.ResultUtil;
import com.atomscat.common.utils.SecurityUtil;
import com.atomscat.common.vo.PageVo;
import com.atomscat.common.vo.Result;
import com.atomscat.common.vo.SearchVo;
import com.atomscat.modules.base.entity.User;
import com.atomscat.modules.resume.entity.ResumeList;
import com.atomscat.modules.resume.entity.TResumeFollowUp;
import com.atomscat.modules.resume.service.ResumeBaseService;
import com.atomscat.modules.resume.service.ResumeFollowUpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Api(description = "简历管理接口")
@RequestMapping("/rmp/resume")
@Transactional
public class ResumeBaseController {

    @Autowired
    private ResumeBaseService resumeBaseService;

    @Autowired
    private ResumeFollowUpService resumeFollowUpService;

    @Autowired
    private SecurityUtil securityUtil;


    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    @ApiOperation(value = "多条件分页获取简历基础列表")
    public Result<Page<ResumeList>> getByCondition(@ModelAttribute ResumeList resumeList,
                                                   @ModelAttribute SearchVo searchVo,
                                                   @ModelAttribute PageVo pageVo) {

        User u = securityUtil.getCurrUser();
        Page<ResumeList> page = resumeBaseService.findPage(resumeList, searchVo, PageUtil.initPage(pageVo), u, null);

        return new ResultUtil<Page<ResumeList>>().setData(page);
    }

    @RequestMapping(value = "/expanded", method = RequestMethod.GET)
    @ApiOperation(value = "默认展开当前行")
    public Result<Object> expanded(@RequestParam boolean expanded, @RequestParam String id) {
        resumeBaseService.expanded(expanded, id);
        return new ResultUtil<Object>().setData("");
    }

    @RequestMapping(value = "/followUp", method = RequestMethod.POST)
    @ApiOperation(value = "简历跟进")
    public Result<Object> followUp(@ModelAttribute TResumeFollowUp tResumeFollowUp) {
        User u = securityUtil.getCurrUser();
        tResumeFollowUp.setSystemUserId(u.getId());
        resumeFollowUpService.followUp(tResumeFollowUp);
        return new ResultUtil<Object>().setData("");
    }

    @RequestMapping(value = "/modifyFollowers", method = RequestMethod.POST)
    @ApiOperation(value = "修改简历跟进人")
    public Result<Object> modifyFollowers(@RequestParam String[] ids, @RequestParam String systemUserId) {
        User u = securityUtil.getCurrUser();
        resumeBaseService.modifyFollowers(ids, systemUserId, u);
        return new ResultUtil<Object>().setData("");
    }

}
