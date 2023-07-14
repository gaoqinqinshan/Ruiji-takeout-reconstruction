package com.gao.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gao.reggie.common.R;
import com.gao.reggie.entity.Category;
import com.gao.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category) {

        log.info("categoey : {}", category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {

        //构造分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapperuery = new LambdaQueryWrapper<>();

        //添加排序条件
        queryWrapperuery.orderByDesc(Category::getSort);

        //执行查询
        categoryService.page(pageInfo, queryWrapperuery);

        return R.success(pageInfo);
    }
}
