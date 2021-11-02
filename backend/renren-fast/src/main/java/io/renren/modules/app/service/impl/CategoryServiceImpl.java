package io.renren.modules.app.service.impl;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.modules.app.dao.CategoryDao;
import io.renren.modules.app.entity.ArticleEntity;
import io.renren.modules.app.entity.CategoryEntity;
import io.renren.modules.app.service.ArticleService;
import io.renren.modules.app.service.CategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Resource
    private ArticleService articleService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> tree() {
        List<CategoryEntity> list = this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().orderByDesc("create_time"));
        List<CategoryEntity> topList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(list)){
            topList = list.stream().filter(item -> item.getLevel() == 0).collect(Collectors.toList());
            for(CategoryEntity c:topList){
                List<CategoryEntity> childList = list.stream().filter(item -> item.getParentId().equals(c.getId())).collect(Collectors.toList());
                c.setChildList(childList);
            }
        }
        return topList;
    }

    @Override
    public List<CategoryEntity> listByName(String name) {
        List<CategoryEntity> list = this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().like("name", name));
        List<CategoryEntity> allList = this.baseMapper.selectList(null);
        if (CollectionUtils.isNotEmpty(list)) {
            for (CategoryEntity c : list) {
                if (c.getLevel() == 0) {
                    List<CategoryEntity> childList = allList.stream().filter(item -> item.getParentId().equals(c.getId())).collect(Collectors.toList());
                    c.setChildList(childList);
                }
            }
        }
        return list;
    }

    @Override
    @Transactional
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        articleService.remove(new QueryWrapper<ArticleEntity>().in("parent_id",idList));
        return super.removeByIds(idList);
    }
}
