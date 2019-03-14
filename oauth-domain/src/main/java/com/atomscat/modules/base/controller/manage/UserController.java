package com.atomscat.modules.base.controller.manage;

import cn.hutool.core.util.StrUtil;
import com.atomscat.common.constant.CommonConstant;
import com.atomscat.common.utils.PageUtil;
import com.atomscat.common.utils.ResultUtil;
import com.atomscat.common.utils.SecurityUtil;
import com.atomscat.common.vo.PageVo;
import com.atomscat.common.vo.Result;
import com.atomscat.common.vo.SearchVo;
import com.atomscat.modules.base.entity.Department;
import com.atomscat.modules.base.entity.Role;
import com.atomscat.modules.base.entity.User;
import com.atomscat.modules.base.entity.UserRole;
import com.atomscat.modules.base.service.DepartmentService;
import com.atomscat.modules.base.service.RoleService;
import com.atomscat.modules.base.service.UserRoleService;
import com.atomscat.modules.base.service.UserService;
import com.atomscat.modules.base.service.mybatis.IUserRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


/**
 * @author Howell Yang
 */
@Slf4j
@RestController
@Api(description = "用户接口")
@RequestMapping("/rmp/user")
@CacheConfig(cacheNames = "user")
@Transactional
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private IUserRoleService iUserRoleService;

    @Autowired
    private UserRoleService userRoleService;


    @Autowired
    private SecurityUtil securityUtil;

    @PersistenceContext
    private EntityManager entityManager;

    @RequestMapping(value = "/regist", method = RequestMethod.POST)
    @ApiOperation(value = "注册用户")
    public Result<Object> regist(@ModelAttribute User u,
                                 @RequestParam String verify,
                                 @RequestParam String captchaId) {

        if (StrUtil.isBlank(verify) || StrUtil.isBlank(u.getUsername())
                || StrUtil.isBlank(u.getPassword())) {
            return new ResultUtil<Object>().setErrorMsg("缺少必需表单字段");
        }

        if (userService.findByUsername(u.getUsername()) != null) {
            return new ResultUtil<Object>().setErrorMsg("该用户名已被注册");
        }

        String encryptPass = new BCryptPasswordEncoder().encode(u.getPassword());
        u.setPassword(encryptPass);
        u.setType(CommonConstant.USER_TYPE_NORMAL);
        User user = userService.save(u);
        if (user == null) {
            return new ResultUtil<Object>().setErrorMsg("注册失败");
        }
        // 默认角色
        List<Role> roleList = roleService.findByDefaultRole(true);
        if (roleList != null && roleList.size() > 0) {
            for (Role role : roleList) {
                UserRole ur = new UserRole();
                ur.setUserId(user.getId());
                ur.setRoleId(role.getId());
                iUserRoleService.insert(ur);
            }
        }

        return new ResultUtil<Object>().setData(user);
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前登录用户接口")
    public Result<User> getUserInfo() {

        User u = securityUtil.getCurrUser();
        // 清除持久上下文环境 避免后面语句导致持久化
        entityManager.clear();
        u.setPassword(null);
        return new ResultUtil<User>().setData(u);
    }

    @RequestMapping(value = "/unlock", method = RequestMethod.POST)
    @ApiOperation(value = "解锁验证密码")
    public Result<Object> unLock(@RequestParam String password) {

        User u = securityUtil.getCurrUser();
        if (!new BCryptPasswordEncoder().matches(password, u.getPassword())) {
            return new ResultUtil<Object>().setErrorMsg("密码不正确");
        }
        return new ResultUtil<Object>().setData(null);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ApiOperation(value = "修改用户自己资料", notes = "用户名密码不会修改 需要username更新缓存")
    @CacheEvict(key = "#u.username")
    public Result<Object> editOwn(@ModelAttribute User u) {

        User old = securityUtil.getCurrUser();
        u.setUsername(old.getUsername());
        u.setPassword(old.getPassword());
        User user = userService.update(u);
        if (user == null) {
            return new ResultUtil<Object>().setErrorMsg("修改失败");
        }
        return new ResultUtil<Object>().setSuccessMsg("修改成功");
    }

    /**
     * @param u
     * @param roles
     * @return
     */
    @RequestMapping(value = "/admin/edit", method = RequestMethod.POST)
    @ApiOperation(value = "管理员修改资料", notes = "需要通过id获取原用户信息 需要username更新缓存")
    @CacheEvict(key = "#u.username")
    public Result<Object> edit(@ModelAttribute User u,
                               @RequestParam(required = false) String[] roles) {

        User old = userService.get(u.getId());


        // 若修改了手机和邮箱判断是否唯一
        if (!old.getMobile().equals(u.getMobile()) && userService.findByMobile(u.getMobile()) != null) {
            return new ResultUtil<Object>().setErrorMsg("该手机号已绑定其他账户");
        }
        if (!old.getEmail().equals(u.getEmail()) && userService.findByMobile(u.getEmail()) != null) {
            return new ResultUtil<Object>().setErrorMsg("该邮箱已绑定其他账户");
        }

        u.setPassword(old.getPassword());
        User user = userService.update(u);
        if (user == null) {
            return new ResultUtil<Object>().setErrorMsg("修改失败");
        }
        //删除该用户角色
        userRoleService.deleteByUserId(u.getId());
        if (roles != null && roles.length > 0) {
            //新角色
            for (String roleId : roles) {
                UserRole ur = new UserRole();
                ur.setRoleId(roleId);
                ur.setUserId(u.getId());
                userRoleService.save(ur);
            }
        }

        return new ResultUtil<Object>().setSuccessMsg("修改成功");
    }

    /**
     * 线上demo不允许测试账号改密码
     *
     * @param password
     * @param newPass
     * @return
     */
    @RequestMapping(value = "/modifyPass", method = RequestMethod.POST)
    @ApiOperation(value = "修改密码")
    public Result<Object> modifyPass(@ApiParam("旧密码") @RequestParam String password,
                                     @ApiParam("新密码") @RequestParam String newPass) {

        User user = securityUtil.getCurrUser();
//        //在线DEMO所需
//        if("test".equals(user.getUsername())||"test2".equals(user.getUsername())){
//            return new ResultUtil<Object>().setErrorMsg("演示账号不支持修改密码");
//        }

        if (!new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            return new ResultUtil<Object>().setErrorMsg("旧密码不正确");
        }

        String newEncryptPass = new BCryptPasswordEncoder().encode(newPass);
        user.setPassword(newEncryptPass);
        userService.update(user);


        return new ResultUtil<Object>().setSuccessMsg("修改密码成功");
    }

    @RequestMapping(value = "/getByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取用户列表")
    public Result<Page<User>> getByCondition(@ModelAttribute User user,
                                             @ModelAttribute SearchVo searchVo,
                                             @ModelAttribute PageVo pageVo) {

        Page<User> page = userService.findByCondition(user, searchVo, PageUtil.initPage(pageVo));
        for (User u : page.getContent()) {
            // 关联部门
            if (StrUtil.isNotBlank(u.getDepartmentId())) {
                Department department = departmentService.get(u.getDepartmentId());
                u.setDepartmentTitle(department.getTitle());
            }
            // 关联角色
            List<Role> list = iUserRoleService.findByUserId(u.getId());
            u.setRoles(list);
            // 清除持久上下文环境 避免后面语句导致持久化
            entityManager.clear();
            u.setPassword(null);
        }
        return new ResultUtil<Page<User>>().setData(page);
    }


    @RequestMapping(value = "/getByDepartmentId/{departmentId}", method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取用户列表")
    public Result<List<User>> getByCondition(@PathVariable String departmentId) {

        List<User> list = userService.findByDepartmentId(departmentId);
        entityManager.clear();
        list.forEach(u -> {
            u.setPassword(null);
        });
        return new ResultUtil<List<User>>().setData(list);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "获取全部用户数据")
    public Result<List<User>> getByCondition() {

        List<User> list = userService.getAll();
        for (User u : list) {
            // 关联部门
            if (StrUtil.isNotBlank(u.getDepartmentId())) {
                Department department = departmentService.get(u.getDepartmentId());
                u.setDepartmentTitle(department.getTitle());
            }
            // 清除持久上下文环境 避免后面语句导致持久化
            entityManager.clear();
            u.setPassword(null);
        }
        return new ResultUtil<List<User>>().setData(list);
    }

    @RequestMapping(value = "/admin/add", method = RequestMethod.POST)
    @ApiOperation(value = "添加用户")
    public Result<Object> regist(@ModelAttribute User u,
                                 @RequestParam(required = false) String[] roles) {

        if (StrUtil.isBlank(u.getUsername()) || StrUtil.isBlank(u.getPassword())) {
            return new ResultUtil<Object>().setErrorMsg("缺少必需表单字段");
        }

        if (userService.findByUsername(u.getUsername()) != null) {
            return new ResultUtil<Object>().setErrorMsg("该用户名已被注册");
        }


        String encryptPass = new BCryptPasswordEncoder().encode(u.getPassword());
        u.setPassword(encryptPass);
        User user = userService.save(u);
        if (user == null) {
            return new ResultUtil<Object>().setErrorMsg("添加失败");
        }
        if (roles != null && roles.length > 0) {
            //添加角色
            for (String roleId : roles) {
                UserRole ur = new UserRole();
                ur.setUserId(u.getId());
                ur.setRoleId(roleId);
                userRoleService.save(ur);
            }
        }

        return new ResultUtil<Object>().setData(user);
    }

    @RequestMapping(value = "/admin/disable/{userId}", method = RequestMethod.POST)
    @ApiOperation(value = "后台禁用用户")
    public Result<Object> disable(@ApiParam("用户唯一id标识") @PathVariable String userId) {

        User user = userService.get(userId);
        if (user == null) {
            return new ResultUtil<Object>().setErrorMsg("通过userId获取用户失败");
        }
        user.setStatus(CommonConstant.USER_STATUS_LOCK);
        userService.update(user);
        //手动更新缓存

        return new ResultUtil<Object>().setData(null);
    }

    @RequestMapping(value = "/admin/enable/{userId}", method = RequestMethod.POST)
    @ApiOperation(value = "后台启用用户")
    public Result<Object> enable(@ApiParam("用户唯一id标识") @PathVariable String userId) {

        User user = userService.get(userId);
        if (user == null) {
            return new ResultUtil<Object>().setErrorMsg("通过userId获取用户失败");
        }
        user.setStatus(CommonConstant.USER_STATUS_NORMAL);
        userService.update(user);
        //手动更新缓存

        return new ResultUtil<Object>().setData(null);
    }

    @RequestMapping(value = "/delByIds/{ids}", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量通过ids删除")
    public Result<Object> delAllByIds(@PathVariable String[] ids) {

        for (String id : ids) {
            User u = userService.get(id);
            //删除缓存
            userService.delete(id);
            //删除关联角色
            userRoleService.deleteByUserId(id);
        }
        return new ResultUtil<Object>().setSuccessMsg("批量通过id删除数据成功");
    }

}
