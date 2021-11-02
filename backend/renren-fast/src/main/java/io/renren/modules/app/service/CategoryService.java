package io.renren.modules.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.app.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 栏目
 *
 * @author houyisoft
 * @email houyisoft@163.com
 * @date 2021-10-29 21:34:56
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> tree();

    List<CategoryEntity> listByName(String name);
}

