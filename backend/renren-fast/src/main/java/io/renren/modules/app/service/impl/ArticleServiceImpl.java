package io.renren.modules.app.service.impl;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.modules.app.dao.ArticleDao;
import io.renren.modules.app.entity.ArticleEntity;
import io.renren.modules.app.entity.CategoryEntity;
import io.renren.modules.app.service.ArticleService;
import io.renren.modules.app.service.CategoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;


@Service("articleService")
public class ArticleServiceImpl extends ServiceImpl<ArticleDao, ArticleEntity> implements ArticleService {
    @Resource
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String parentId = (String) params.getOrDefault("parentId","");
        IPage<ArticleEntity> page = this.page(
                new Query<ArticleEntity>().getPage(params),
                new QueryWrapper<ArticleEntity>().eq(StringUtils.isNotBlank(parentId),"parent_id",parentId).orderByDesc("updated_time")
        );

        return new PageUtils(page);
    }

    @Override
    public ArticleEntity getById(Serializable id) {
        ArticleEntity entity = super.getById(id);
        CategoryEntity two = categoryService.getById(entity.getParentId());
        entity.setTwoCategoryName(two==null?"":two.getName());
        if(two!=null){
            CategoryEntity one = categoryService.getById(two.getParentId());
            entity.setOneCategoryName(one==null?"":one.getName());
        }
        return entity;
    }
}
