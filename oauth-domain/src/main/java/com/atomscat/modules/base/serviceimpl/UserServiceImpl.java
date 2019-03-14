package com.atomscat.modules.base.serviceimpl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.atomscat.common.utils.SecurityUtil;
import com.atomscat.common.vo.SearchVo;
import com.atomscat.modules.base.dao.DepartmentDao;
import com.atomscat.modules.base.dao.UserDao;
import com.atomscat.modules.base.dao.mapper.PermissionMapper;
import com.atomscat.modules.base.dao.mapper.UserRoleMapper;
import com.atomscat.modules.base.entity.Department;
import com.atomscat.modules.base.entity.Permission;
import com.atomscat.modules.base.entity.Role;
import com.atomscat.modules.base.entity.User;
import com.atomscat.modules.base.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户接口实现
 *
 * @author Howell Yang
 */
@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private SecurityUtil securityUtil;

    @Override
    public UserDao getRepository() {
        return userDao;
    }

    @Override
    public User findByUsername(String username) {

        List<User> list = userDao.findByUsername(username);
        if (list != null && list.size() > 0) {
            User user = list.get(0);
            // 关联部门
            if (StrUtil.isNotBlank(user.getDepartmentId())) {
                Department department = departmentDao.getOne(user.getDepartmentId());
                user.setDepartmentTitle(department.getTitle());
            }
            // 关联角色
            List<Role> roleList = userRoleMapper.findByUserId(user.getId());
            user.setRoles(roleList);
            // 关联权限菜单
            List<Permission> permissionList = permissionMapper.findByUserId(user.getId());
            user.setPermissions(permissionList);
            return user;
        }
        return null;
    }

    @Override
    public User findByMobile(String mobile) {

        List<User> list = userDao.findByMobile(mobile);
        if (list != null && list.size() > 0) {
            User user = list.get(0);
            return user;
        }
        return null;
    }

    @Override
    public User findByEmail(String email) {

        List<User> list = userDao.findByEmail(email);
        if (list != null && list.size() > 0) {
            User user = list.get(0);
            return user;
        }
        return null;
    }

    @Override
    public Page<User> findByCondition(User user, SearchVo searchVo, Pageable pageable) {

        return userDao.findAll(new Specification<User>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

                Path<String> usernameField = root.get("username");
                Path<String> mobileField = root.get("mobile");
                Path<String> emailField = root.get("email");
                Path<String> departmentIdField = root.get("departmentId");
                Path<Integer> sexField = root.get("sex");
                Path<Integer> typeField = root.get("type");
                Path<Integer> statusField = root.get("status");
                Path<Date> createTimeField = root.get("createTime");

                List<Predicate> list = new ArrayList<Predicate>();

                //模糊搜素
                if (StrUtil.isNotBlank(user.getUsername())) {
                    list.add(cb.like(usernameField, '%' + user.getUsername() + '%'));
                }
                if (StrUtil.isNotBlank(user.getMobile())) {
                    list.add(cb.like(mobileField, '%' + user.getMobile() + '%'));
                }
                if (StrUtil.isNotBlank(user.getEmail())) {
                    list.add(cb.like(emailField, '%' + user.getEmail() + '%'));
                }

                //部门
                if (StrUtil.isNotBlank(user.getDepartmentId())) {
                    list.add(cb.equal(departmentIdField, user.getDepartmentId()));
                }

                //性别
                if (user.getSex() != null) {
                    list.add(cb.equal(sexField, user.getSex()));
                }
                //类型
                if (user.getType() != null) {
                    list.add(cb.equal(typeField, user.getType()));
                }
                //状态
                if (user.getStatus() != null) {
                    list.add(cb.equal(statusField, user.getStatus()));
                }
                //创建时间
                if (StrUtil.isNotBlank(searchVo.getStartDate()) && StrUtil.isNotBlank(searchVo.getEndDate())) {
                    Date start = DateUtil.parse(searchVo.getStartDate());
                    Date end = DateUtil.parse(searchVo.getEndDate());
                    list.add(cb.between(createTimeField, start, DateUtil.endOfDay(end)));
                }

                //数据权限
                List<String> depIds = securityUtil.getDeparmentIds();
                if (depIds != null && depIds.size() > 0) {
                    list.add(departmentIdField.in(depIds));
                }

                Predicate[] arr = new Predicate[list.size()];
                cq.where(list.toArray(arr));
                return null;
            }
        }, pageable);
    }

    @Override
    public List<User> findByDepartmentId(String departmentId) {

        return userDao.findByDepartmentId(departmentId);
    }
}
