package com.gao.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gao.reggie.common.R;
import com.gao.reggie.entity.Employee;
import com.gao.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import sun.security.util.Password;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登陆
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        //密码Md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //根据页面提交的用户名username查找数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //如果没有查到则返回失败结果
        if (emp == null) {
            return R.error("登录失败");
        }

        //密码比对，如果不一致返回失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }

        //查看员工状态，如果禁用则返回员工禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账号已经禁用");
        }

        //登陆成功，把员工id存到Session并且返回登陆成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     *
     * @param request
     * @return
     */

    @PostMapping("/logout")
    public R<String> logour(HttpServletRequest request) {

        //清理Session中保存的当前登陆的员工id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     *
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工成功，员工为：{}", employee.toString());

        //设置密码，并且md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);


        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 根据id查询员工信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }

    /**
     * 员工信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page = {},pageSize = {} , name = {}", page, pageSize, name);

        //构造分页构造器
        Page pageinfo = new Page(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

        //添加过滤条件
        queryWrapper.like(StringUtils.hasText(name), Employee::getName, name);

        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageinfo, queryWrapper);

        return R.success(pageinfo);
    }

    /**
     * 根据id修改员工信息
     *
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> updata(HttpServletRequest request, @RequestBody Employee employee) {
        log.info(employee.toString());

        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);
        employeeService.updateById(employee);

        return R.success("员工信息更新成功");
    }
}
