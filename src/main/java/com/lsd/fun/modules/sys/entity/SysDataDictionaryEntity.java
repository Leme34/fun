package com.lsd.fun.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据字典
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2019-11-15 11:03:15
 */
@Data
@TableName("sys_data_dictionary")
public class SysDataDictionaryEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId
	private Integer id;
	/**
	 * 父节点id
	 */
	private Integer pid;
	/**
	 * 名称
	 */
	private String name;
	/**
	 *
	 */
	private LocalDateTime createdAt;
	/**
	 *
	 */
	private LocalDateTime updatedAt;

}
