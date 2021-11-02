package io.renren.modules.app.dao;

import io.renren.modules.app.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 栏目
 *
 * @author houyisoft
 * @email houyisoft@163.com
 * @date 2021-10-29 21:34:56
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {

}
