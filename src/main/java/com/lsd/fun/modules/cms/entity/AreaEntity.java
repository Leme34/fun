package com.lsd.fun.modules.cms.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 地区表,直辖市在level=0能够找到，在level=1也能找到
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-26 02:06:00
 */
@Accessors(chain = true)
@Data
@TableName("area")
public class AreaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId
	private Integer id;
	/**
	 * 父级id（一级为0）
	 */
	private Integer pid;
	/**
	 * 地区名
	 */
	private String name;
	/**
	 * 0:省份/直辖市,1:市级单位,2:区级单位
	 */
	private Integer level;
	/**
	 *
	 */
	private LocalDateTime createdAt;
	/**
	 *
	 */
	private LocalDateTime updatedAt;

	/**
	 * 子地区
	 */
	@TableField(exist = false)
	private List<AreaEntity> subAreas;

}
