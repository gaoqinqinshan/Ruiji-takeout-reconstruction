package com.gao.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gao.reggie.common.CustomExcption;
import com.gao.reggie.entity.Category;
import com.gao.reggie.entity.Dish;
import com.gao.reggie.entity.Setmeal;
import com.gao.reggie.mapper.CategoryMapper;
import com.gao.reggie.service.CategoryService;
import com.gao.reggie.service.DishService;
import com.gao.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类（需要判断）
     *
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);

        int count1 = dishService.count(dishLambdaQueryWrapper);

        //判断当前的分类是否关联了菜品，如果是抛出异常
        if (count1 > 0) {
            throw new CustomExcption("当前分类关联菜品，不能删除");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        //判断当前的分类是否关联了套餐，如果是抛出异常

        if (count2 > 0) {
            throw new CustomExcption("当前分类关联套餐，不能删除");
        }
        //正常删除
        super.removeById(id);
    }
}
