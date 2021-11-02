package io.renren.modules.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 栏目
 *
 * @author houyisoft
 * @email houyisoft@163.com
 * @date 2021-10-29 21:34:56
 */
@Data
@TableName("tb_category")
public class CategoryEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId
	@TableField(value = "id")
	private Long id;
	/**
	 * 名称
	 */
	@TableField(value = "name")
	private String name;
	/**
	 * 父id
	 */
	@TableField(value = "parent_id")
	private Long parentId;
	/**
	 * 级别|0：一级1：二级
	 */
	@TableField(value = "level")
	private Integer level;
	/**
	 * 排序字段|预留
	 */
	@TableField(value = "sort_num")
	private Integer sortNum;
	/**
	 * 创建时间
	 */
	@TableField(value = "create_time")
	@JsonFormat(pattern="yyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createTime;

	/**
	 * 子目录
	 */
	@TableField(exist = false)
	private List<CategoryEntity> childList;
}
