package com.atomscat.modules.resume.serviceimpl;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.atomscat.modules.resume.dao.mapper.TResumeBasisMapper;
import com.atomscat.modules.resume.dao.mapper.TResumeFollowUpMapper;
import com.atomscat.modules.resume.entity.TResumeBasis;
import com.atomscat.modules.resume.entity.TResumeFollowUp;
import com.atomscat.modules.resume.service.ResumeFollowUpService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
@Transactional
public class ResumeFollowUpServiceImpl implements ResumeFollowUpService {

    @Resource
    private TResumeFollowUpMapper tResumeFollowUpMapper;

    @Resource
    private TResumeBasisMapper tResumeBasisMapper;

    /**
     * 简历跟进
     * @param tResumeFollowUp
     * @return
     */
    public boolean followUp(TResumeFollowUp tResumeFollowUp) {

        TResumeBasis basis = tResumeBasisMapper.selectById(tResumeFollowUp.getResumeId());
        if(basis.getSystemUserId().equals(tResumeFollowUp.getSystemUserId()) && basis.getLevel() != 1 ) { // 只有跟进人,客户等级不是1,才有权限修改
            String id = UUID.fastUUID().toString().replace("-", "");
            tResumeFollowUp.setId(id);
            tResumeFollowUp.setCreateTime(new Date());
            if (tResumeFollowUpMapper.insert(tResumeFollowUp) > 0) {
                EntityWrapper entityWrapper = new EntityWrapper();
                entityWrapper.eq("id", tResumeFollowUp.getResumeId());

                TResumeBasis tResumeBasis = new TResumeBasis();
                tResumeBasis.setProduct(tResumeFollowUp.getProduct());
                tResumeBasis.setSystemUserId(tResumeFollowUp.getSystemUserId());
                tResumeBasis.setLevel(tResumeFollowUp.getLevel());
                tResumeBasis.setFollowStatus(tResumeFollowUp.getFollowStatus());
                tResumeBasis.setUpdateTime(new Date());
                if(!StringUtils.isEmpty(tResumeFollowUp.getNextFollow())) {
                    String[] strings = tResumeFollowUp.getNextFollow().split("~");
                    tResumeBasis.setNextFollow(DateUtil.parse(strings[0], "yyyy-MM-dd"));
                    tResumeBasis.setNextFollowEnd(DateUtil.parse(strings[1], "yyyy-MM-dd"));
                } else {
                    tResumeBasis.setNextFollow(null);
                    tResumeBasis.setNextFollowEnd(null);
                }
                tResumeBasisMapper.update(tResumeBasis, entityWrapper);
                return true;
            }
        }
        return false;
    }

}
