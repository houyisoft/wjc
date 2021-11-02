package io.renren.modules.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文章
 *
 * @author houyisoft
 * @email houyisoft@163.com
 * @date 2021-10-29 21:34:56
 */
@Data
@TableName("tb_article")
public class ArticleEntity implements Serializable {
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
	@TableField(value = "title")
	private String title;
	/**
	 * 内容
	 */
	@TableField(value = "content")
	private String content;
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
	 * 创建时间
	 */
	@TableField(value = "created_time")
	@JsonFormat(pattern="yyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createdTime;
	/**
	 * 修改时间
	 */
	@TableField(value = "updated_time")
	@JsonFormat(pattern="yyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date updatedTime;
	/**
	 * 创建人id
	 */
	@TableField(value = "created_user_id")
	private Long createdUserId;
	/**
	 * 一级目录名称
	 */
	@TableField(exist = false)
	private String oneCategoryName;
	/**
	 * 二级目录名称
	 */
	@TableField(exist = false)
	private String twoCategoryName;
}
