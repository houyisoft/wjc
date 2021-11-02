package io.renren.modules.app.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.app.annotation.Login;
import io.renren.modules.app.annotation.LoginUser;
import io.renren.modules.app.entity.CategoryEntity;
import io.renren.modules.app.service.CategoryService;
import org.springframework.web.bind.annotation.*;


/**
 * 栏目
 *
 * @author houyisoft
 * @email houyisoft@163.com
 * @date 2021-10-29 21:34:56
 */
@RestController
@RequestMapping("/app/category")
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    /**
     * 列表
     */
    @RequestMapping("/pageList")
    public R pageList(@RequestParam Map<String, Object> params){
        PageUtils page = categoryService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/{id}")
    public R info(@PathVariable("id") Long id){
        CategoryEntity category = categoryService.getById(id);
        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @Login
    @RequestMapping("/save")
    public R save(@RequestBody CategoryEntity entity){
        entity.setCreateTime(new Date());
        categoryService.save(entity);
        return R.ok();
    }

    /**
     * 修改
     */
    @Login
    @RequestMapping("/update")
    public R update(@RequestBody CategoryEntity entity){
        //ValidatorUtils.validateEntity(category);
        categoryService.updateById(entity);
        return R.ok();
    }

    /**
     * 删除
     */
    @Login
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        categoryService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

    @RequestMapping("/tree")
    public R tree(){
         List<CategoryEntity> list = categoryService.tree();
         return R.ok().put("data",list);
    }

    @RequestMapping("/listByName")
    public R listByName(String name){
        List<CategoryEntity> list = categoryService.listByName(name);
        return R.ok().put("data",list);
    }
}
