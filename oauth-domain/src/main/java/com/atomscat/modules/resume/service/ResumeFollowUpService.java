package com.atomscat.modules.resume.service;


import com.atomscat.modules.resume.entity.TResumeFollowUp;

public interface ResumeFollowUpService {
    /**
     * 简历跟进
     * @param tResumeFollowUp
     * @return
     */
    boolean followUp(TResumeFollowUp tResumeFollowUp);
}
