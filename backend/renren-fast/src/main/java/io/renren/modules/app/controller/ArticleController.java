package io.renren.modules.app.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.common.validator.Assert;
import io.renren.modules.app.annotation.Login;
import io.renren.modules.app.entity.ArticleEntity;
import io.renren.modules.app.entity.CategoryEntity;
import io.renren.modules.app.service.ArticleService;
import io.renren.modules.app.service.CategoryService;
import org.springframework.web.bind.annotation.*;


/**
 * 文章
 *
 * @author houyisoft
 * @email houyisoft@163.com
 * @date 2021-10-29 21:34:56
 */
@RestController
@RequestMapping("/app/article")
public class ArticleController {
    @Resource
    private ArticleService articleService;
    @Resource
    private CategoryService categoryService;

    /**
     * 列表
     */
    @RequestMapping("/pageList")
    public R pageList(@RequestParam Map<String, Object> params){
        PageUtils page = articleService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @GetMapping("/{id}")
    public R info(@PathVariable("id") Long id){
        ArticleEntity article = articleService.getById(id);
        return R.ok().put("article", article);
    }

    /**
     * 保存
     */
    @Login
    @RequestMapping("/save")
    public R save(@RequestBody ArticleEntity entity){
        CategoryEntity categoryEntity = categoryService.getById(entity.getParentId());
        Assert.isNull(categoryEntity,"查无此目录!");
        if(categoryEntity.getLevel()==0){
            return R.error("只能在二级目录创建文章!");
        }
        entity.setCreatedTime(new Date());
        entity.setUpdatedTime(new Date());
        articleService.save(entity);
        return R.ok();
    }

    /**
     * 修改
     */
    @Login
    @RequestMapping("/update")
    public R update(@RequestBody ArticleEntity entity){
        CategoryEntity categoryEntity = categoryService.getById(entity.getParentId());
        Assert.isNull(categoryEntity,"查无此目录!");
        if(categoryEntity.getLevel()==0){
            return R.error("只能在二级目录创建文章!");
        }
        //ValidatorUtils.validateEntity(article);
        entity.setUpdatedTime(new Date());
        articleService.updateById(entity);
        return R.ok();
    }

    /**
     * 删除
     */
    @Login
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        articleService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }


}
