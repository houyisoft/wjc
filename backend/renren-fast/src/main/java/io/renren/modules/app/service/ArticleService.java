package io.renren.modules.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.app.entity.ArticleEntity;

import java.util.Map;

/**
 * 文章
 *
 * @author houyisoft
 * @email houyisoft@163.com
 * @date 2021-10-29 21:34:56
 */
public interface ArticleService extends IService<ArticleEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

