package com.lsd.fun.modules.cos.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 文件对象表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2019-11-28 16:23:40
 */
@Accessors(chain = true)
@Data
@TableName("t_file")
public class TFileEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId
	private Long id;
	/**
	 * 原始文件名
	 */
	private String originalFilename;
	/**
	 * 存储路径
	 */
	private String path;
	/**
	 * 对象大小（字节）
	 */
	private Long size;
	/**
	 * MIME类型
	 */
	private String mimeType;
	/**
	 * 上传用户id
	 */
	private Long uploaderId;
	/**
	 *
	 */
	private LocalDateTime createdAt;
	/**
	 *
	 */
	private LocalDateTime updatedAt;
	/**
	 *
	 */
	private LocalDateTime deletedAt;

	private Integer isCrawl;

}
