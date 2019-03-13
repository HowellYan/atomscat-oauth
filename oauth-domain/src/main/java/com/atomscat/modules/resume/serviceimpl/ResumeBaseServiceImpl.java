package com.atomscat.modules.resume.serviceimpl;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.atomscat.common.vo.SearchVo;
import com.atomscat.modules.base.dao.mapper.UserMapper;
import com.atomscat.modules.base.entity.User;
import com.atomscat.modules.resume.dao.TResumeBasisDao;
import com.atomscat.modules.resume.dao.mapper.TResumeBasisMapper;
import com.atomscat.modules.resume.dao.mapper.TResumeEducationMapper;
import com.atomscat.modules.resume.dao.mapper.TResumeFollowUpMapper;
import com.atomscat.modules.resume.dao.mapper.TResumePostMapper;
import com.atomscat.modules.resume.entity.*;
import com.atomscat.modules.resume.service.ResumeBaseService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ResumeBaseServiceImpl implements ResumeBaseService {

    @Autowired
    private TResumeBasisDao tResumeBasisDao;

    @Resource
    private TResumeEducationMapper tResumeEducationMapper;

    @Resource
    private TResumeFollowUpMapper tResumeFollowUpMapper;

    @Resource
    private TResumePostMapper tResumePostMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private TResumeBasisMapper tResumeBasisMapper;

    @Override
    public TResumeBasisDao getRepository() {
        return tResumeBasisDao;
    }


    /**
     * 多条件分页获取简历基础
     * @param tResumeBasis
     * @param searchVo
     * @param pageable
     * @param u
     * @param ids 导出选择的数据id
     * @return
     */
    @Override
    public Page<ResumeList> findPage(ResumeList tResumeBasis, SearchVo searchVo, Pageable pageable, User u, String[] ids) {
        Page<ResumeList> tResumeBasisPage = tResumeBasisDao.findAll(new Specification<ResumeList>() {
            @Override
            public Predicate toPredicate(Root<ResumeList> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();


                // todo 条件搜索
                Path<String> systemUserId = root.get("systemUserId");
                Path<String> applyPosition = root.get("applyPosition");
                Path<String> level = root.get("level");
                Path<String> followStatus = root.get("followStatus");
                Path<String> sex = root.get("sex");
                Path<String> name = root.get("name");
                Path<String> currentResidence = root.get("currentResidence");
                Path<Integer> workingYears = root.get("workingYears");
                Path<String> contactNumber = root.get("contactNumber");
                Path<String> degree = root.get("degree");
                Expression<String> id = root.get("id");

                //模糊搜素
                if (StrUtil.isNotBlank(tResumeBasis.getSystemUserId())) { // 跟进人
                    list.add(cb.like(systemUserId, '%' + tResumeBasis.getSystemUserId() + '%'));
                }
                if (StrUtil.isNotBlank(tResumeBasis.getApplyPosition())) { // 应聘职位
                    list.add(cb.like(applyPosition, '%' + tResumeBasis.getApplyPosition() + '%'));
                }
                if (tResumeBasis.getLevel() != null) { // 客户级别
                    list.add(cb.equal(level, tResumeBasis.getLevel()));
                }
                if (tResumeBasis.getFollowStatus() != null) { // 状态
                    list.add(cb.equal(followStatus, tResumeBasis.getFollowStatus()));
                }
                if (tResumeBasis.getSex() != null) { // 性别
                    list.add(cb.equal(sex, tResumeBasis.getSex()));
                }
                if (StrUtil.isNotBlank(tResumeBasis.getName())) { // 姓名
                    list.add(cb.like(name, '%' + tResumeBasis.getName() + '%'));
                }
                if (StrUtil.isNotBlank(tResumeBasis.getCurrentResidence())) { // 目前居住地
                    list.add(cb.like(currentResidence, '%' + tResumeBasis.getCurrentResidence() + '%'));
                }
                if (StrUtil.isNotBlank(tResumeBasis.getContactNumber())) {
                    list.add(cb.like(contactNumber, '%' + tResumeBasis.getContactNumber() + '%'));
                }
                if (StrUtil.isNotBlank(tResumeBasis.getDegree())) {
                    list.add(cb.like(degree, '%' + tResumeBasis.getDegree() + '%'));
                }

                if (tResumeBasis.getStartNum() != null && tResumeBasis.getEndNum() != null && tResumeBasis.getStartNum() <= tResumeBasis.getEndNum()) {
                    list.add(cb.between(workingYears, tResumeBasis.getStartNum(), tResumeBasis.getEndNum()));
                }

                if (ids != null && ids.length > 0) {
                    List<String> stringList = new ArrayList<>();
                    for (String s : ids) {
                        stringList.add(s);
                    }
                    list.add(id.in(stringList));
                }

                Path<Date> dateOfBirth = root.get("dateOfBirth");
                //创建时间
                if (StrUtil.isNotBlank(searchVo.getStartDate()) && StrUtil.isNotBlank(searchVo.getEndDate())) {
                    Date start = DateUtil.parse(searchVo.getStartDate());
                    Date end = DateUtil.parse(searchVo.getEndDate());
                    list.add(cb.between(dateOfBirth, start, DateUtil.endOfDay(end)));
                }
                Predicate[] arr = new Predicate[list.size()];
                cq.where(list.toArray(arr));
                return null;
            }
        }, pageable);

        List<String> stringCollection = new ArrayList<>(); // 简历 id List
        List<String> systemUserIdList = new ArrayList<>(); // 简历 跟进人ID List
        tResumeBasisPage.forEach((item) -> {
            stringCollection.add(item.getId());
            systemUserIdList.add(item.getSystemUserId()); //基础表 跟进人ID
            if (item.getSystemUserId().equals(u.getId())) {
                item.set_canFollow(true);
            } else {
                item.set_canFollow(false);
            }
            if(item.getNextFollow()!= null && item.getNextFollow().before(new Date())) {
                item.set_expired(true);
            } else {
                item.set_expired(false);
            }
        });
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.in("resume_id", stringCollection).orderBy("create_time");
        List<TResumeEducation> tResumeEducationList = tResumeEducationMapper.selectList(entityWrapper);
        List<TResumeFollowUp> tResumeFollowUpList = tResumeFollowUpMapper.selectList(entityWrapper);
        List<TResumePost> tResumePostList = tResumePostMapper.selectList(entityWrapper);


        tResumeFollowUpList.forEach((i) -> {
            systemUserIdList.add(i.getSystemUserId()); //跟进表 跟进人ID
        });
        // 去除List中重复的String
        List<String> list = systemUserIdList.stream().distinct().collect(Collectors.toList());
        entityWrapper = new EntityWrapper();
        entityWrapper.in("id", list);
        List<User> userList = userMapper.selectList(entityWrapper);

        tResumeBasisPage.forEach((item) -> {
            // 跟进人id 转 跟进人name
            userList.forEach((user) -> {
                if (item.getSystemUserId().equals(user.getId())) {
                    item.setSystemUserName(user.getUsername());
                }
            });

            // 添加简历教育
            List<TResumeEducation> tResumeEducations = new ArrayList<>();
            String id = item.getId();
            tResumeEducationList.forEach((i) -> {
                if (i.getResumeId().equals(id)) {
                    tResumeEducations.add(i);
                }
            });
            item.setTResumeEducationList(tResumeEducations);

            // 添加简历岗位
            List<TResumePost> tResumePosts = new ArrayList<>();
            tResumePostList.forEach((i) -> {
                if (i.getResumeId().equals(id)) {
                    tResumePosts.add(i);
                }
            });
            item.setTResumePostList(tResumePosts);

            // 添加简历跟进详情
            List<TResumeFollowUp> tResumeFollowUps = new ArrayList<>();
            tResumeFollowUpList.forEach((i) -> {
                if (i.getResumeId().equals(id)) {
                    userList.forEach((user) -> {  // 跟进人id 转 跟进人name
                        if (i.getSystemUserId().equals(user.getId())) {
                            i.setSystemUserName(user.getUsername());
                        }
                    });
                    tResumeFollowUps.add(i);
                }
            });
            item.setTResumeFollowUpList(tResumeFollowUps);
        });
        return tResumeBasisPage;
    }

    /**
     * 默认展开当前行
     *
     * @param expanded
     * @param id
     * @return
     */
    @Override
    public boolean expanded(boolean expanded, String id) {
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("id", id);
        TResumeBasis tResumeBasis = new TResumeBasis();
        tResumeBasis.set_expanded(expanded);
        tResumeBasisMapper.update(tResumeBasis, entityWrapper);
        return false;
    }

    /**
     * 修改简历跟进人
     *
     * @param ids
     * @param systemUserId
     * @return
     */
    @Override
    public boolean modifyFollowers(String[] ids, String systemUserId, User u) {
        for (String resumeId : ids) {
            TResumeBasis basis = tResumeBasisMapper.selectById(resumeId);
            if(basis.getLevel() != 1 ) { // 只有客户等级不是1,才有权限修改
                EntityWrapper entityWrapper = new EntityWrapper();
                entityWrapper.eq("id", resumeId);
                TResumeBasis tResumeBasis = new TResumeBasis();
                tResumeBasis.setSystemUserId(systemUserId);
                tResumeBasisMapper.update(tResumeBasis, entityWrapper);

                TResumeFollowUp tResumeFollowUp = new TResumeFollowUp();
                String id = UUID.fastUUID().toString().replace("-", "");
                tResumeFollowUp.setId(id);
                tResumeFollowUp.setResumeId(resumeId);
                tResumeFollowUp.setSystemUserId(systemUserId);
                tResumeFollowUp.setRemarks("由" + u.getUsername() + "(" + u.getId() + ")" + "修改跟进人");
                tResumeFollowUp.setCreateBy(u.getUsername() + "(" + u.getId() + ")");
                tResumeFollowUp.setCreateTime(new Date());
                tResumeFollowUpMapper.insert(tResumeFollowUp);
            }
        }
        return false;
    }

    /**
     * xls 导入
     *
     * @param mapList
     * @return
     */
    @Override
    public ImportFileResp importFile(List<Map<String, Object>> mapList, User u) {
        ImportFileResp importFileResp = new ImportFileResp();
        importFileResp.setIsRepeat(false);
        mapList.forEach((item) -> {
            try {
                String id = UUID.fastUUID().toString().replace("-", "");
                TResumeBasis tResumeBasis = new TResumeBasis();
                tResumeBasis.setId(id);
                tResumeBasis.setName(String.valueOf(item.get("姓名")).replaceAll(" ", ""));
                tResumeBasis.setContactNumber(String.valueOf(item.get("联系电话")).replaceAll(" ", ""));
                if (item.get("性别").equals("男")) {
                    tResumeBasis.setSex(1); // 男
                } else {
                    tResumeBasis.setSex(0); // 女
                }
                try {
                    int workingYears = Integer.parseInt(((String) item.get("工作年限")).replaceAll("年", ""));
                    tResumeBasis.setWorkingYears(workingYears);
                } catch (Exception e) {
                    tResumeBasis.setWorkingYears(0);
                }
                tResumeBasis.setEMail(String.valueOf(item.get("电子邮件")));
                tResumeBasis.setExpectedSalary((String) item.get("期望薪资"));
                tResumeBasis.setSystemUserId("682265633886208"); // 默認跟进人 : admin
                tResumeBasis.setLevel(6);
                tResumeBasis.setPublishCity((String) item.get("发布城市")); // 发布城市
                tResumeBasis.setApplyPosition((String) item.get("应聘职位")); // 应聘职位
                tResumeBasis.setApplyTime(String.valueOf(item.get("应聘日期"))); //应聘日期
                tResumeBasis.setApplyCompany((String) item.get("应聘公司")); //应聘公司
                tResumeBasis.setNumber(String.valueOf(item.get("简历编号")));// 简历编号
                tResumeBasis.setCurrentResidence((String) item.get("目前居住地")); // 目前居住地
                tResumeBasis.setAddress((String) item.get("地址")); //地址
                tResumeBasis.setZipCode(String.valueOf(item.get("邮编"))); //邮编
                tResumeBasis.setAnnualIncome(String.valueOf(item.get("目前年收入")));
                tResumeBasis.setDegree((String) item.get("学历/学位"));

                if (!StringUtils.isEmpty(item.get("出生日期"))) {
                    String regex2 = "[0-9]{4}-[0-9]{2}";
                    String regex3 = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
                    String regex4 = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}";
                    String str = String.valueOf(item.get("出生日期"));
                    if (String.valueOf(item.get("出生日期")).contains("/")) {
                        tResumeBasis.setDateOfBirth(DateUtil.parse(str, "yyyy/MM/dd")); //出生日期
                    } else if (Pattern.compile(regex2).matcher(str).matches()) {
                        tResumeBasis.setDateOfBirth(DateUtil.parse(str, "yyyy-MM")); //出生日期
                    } else if(Pattern.compile(regex3).matcher(str).matches()){
                        tResumeBasis.setDateOfBirth(DateUtil.parse(str, "yyyy-MM-dd")); //出生日期
                    } else if(Pattern.compile(regex4).matcher(str).matches()){
                        tResumeBasis.setDateOfBirth(DateUtil.parse(str, "yyyy-MM-dd HH:mm:ss")); //出生日期
                    } else {
                        item.put("错误警告", "日期格式不正确（单元格格式是`常规`：yyyy/MM/dd）");
                        importFileResp.setIsRepeat(true);
                    }
                }
                tResumeBasis.setDataSources((String) item.get("数据来源"));
                if (item.get("求职状态").equals("目前正在找工作")) { //目前正在找工作
                    tResumeBasis.setJobSearchingStatus(1);
                } else if (item.get("求职状态").equals("我目前不想换工作")) {
                    tResumeBasis.setJobSearchingStatus(2);
                } else if (item.get("求职状态").equals("观望有好机会会考虑")) {
                    tResumeBasis.setJobSearchingStatus(3);
                }
                tResumeBasisMapper.insert(tResumeBasis);

                TResumeEducation tResumeEducation = new TResumeEducation();
                String education_id = UUID.fastUUID().toString().replace("-", "");
                tResumeEducation.setId(education_id);
                tResumeEducation.setResumeId(id);
                tResumeEducation.setSchool((String) item.get("毕业学校"));
                tResumeEducation.setProfession((String) item.get("专业"));
                tResumeEducation.setDegree((String) item.get("学历/学位"));
                tResumeEducationMapper.insert(tResumeEducation);

                TResumePost tResumePost = new TResumePost();
                String post_id = UUID.fastUUID().toString().replace("-", "");
                tResumePost.setId(post_id);
                tResumePost.setResumeId(id);
                tResumePost.setCompany((String) item.get("最近一家公司"));
                tResumePost.setPosition((String) item.get("最近一个职位"));
                tResumePost.setAnnualIncome((String) item.get("目前年收入"));
                tResumePostMapper.insert(tResumePost);

                TResumeFollowUp tResumeFollowUp = new TResumeFollowUp();
                String tResumeFollowUp_id = UUID.fastUUID().toString().replace("-", "");
                tResumeFollowUp.setId(tResumeFollowUp_id);
                tResumeFollowUp.setResumeId(id);
                tResumeFollowUp.setSystemUserId("682265633886208"); // 默認跟进人 : admin
                tResumeFollowUp.setRemarks("由" + u.getUsername() + "(" + u.getId() + ")" + "导入数据");
                tResumeFollowUp.setCreateBy(u.getUsername() + "(" + u.getId() + ")");
                tResumeFollowUp.setCreateTime(new Date());
                tResumeFollowUpMapper.insert(tResumeFollowUp);

            } catch (DuplicateKeyException e) {
                item.put("错误警告", "数据重复");
                importFileResp.setIsRepeat(true);
            } catch (ClassCastException e) {
                if (e.getMessage().contains("cn.hutool.core.date.DateTime")) {
                    item.put("错误警告", "日期格式不正确（单元格格式是`常规`：yyyy/MM/dd）");
                } else {
                    item.put("错误警告", "类型转换错误：" + e.getMessage());
                    log.error(e.getMessage());
                }
                importFileResp.setIsRepeat(true);
            } catch (Exception e) {
                item.put("错误警告", e.getMessage());
                importFileResp.setIsRepeat(true);
            }
        });
        importFileResp.setList(mapList);
        return importFileResp;
    }


    /**
     * xls 导入
     *
     * @param mapList
     * @return
     */
    @Override
    public void importFileUpdateTime(List<Map<String, Object>> mapList) {
        mapList.forEach((item) -> {
            try {
                TResumeBasis tResumeBasis = new TResumeBasis();
                String name = String.valueOf(item.get("姓名")).replaceAll(" ", "");
                String contactNumber = String.valueOf(item.get("联系电话")).replaceAll(" ", "");

                if (!StringUtils.isEmpty(item.get("出生日期"))) {
                    String regex2 = "[0-9]{4}-[0-9]{2}";
                    String regex3 = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
                    String regex4 = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}";
                    String str = String.valueOf(item.get("出生日期"));
                    if (String.valueOf(item.get("出生日期")).contains("/")) {
                        tResumeBasis.setDateOfBirth(DateUtil.parse(str, "yyyy/MM/dd")); //出生日期
                    } else if (Pattern.compile(regex2).matcher(str).matches()) {
                        tResumeBasis.setDateOfBirth(DateUtil.parse(str, "yyyy-MM")); //出生日期
                    } else if (Pattern.compile(regex3).matcher(str).matches()) {
                        tResumeBasis.setDateOfBirth(DateUtil.parse(str, "yyyy-MM-dd")); //出生日期
                    } else if (Pattern.compile(regex4).matcher(str).matches()) {
                        tResumeBasis.setDateOfBirth(DateUtil.parse(str, "yyyy-MM-dd HH:mm:ss")); //出生日期
                    }
                }
                EntityWrapper<TResumeBasis> tResumeBasisEntityWrapper = new EntityWrapper<>();
                tResumeBasisEntityWrapper.eq("name", name).eq("contact_number", contactNumber);
                tResumeBasisMapper.update(tResumeBasis, tResumeBasisEntityWrapper);
            } catch (Exception e){
                log.error(e.getMessage());
            }
        });
    }

}
