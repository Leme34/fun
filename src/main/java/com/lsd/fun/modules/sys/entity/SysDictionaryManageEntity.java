package com.lsd.fun.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import com.lsd.fun.common.validator.group.AddGroup;
import com.lsd.fun.common.validator.group.UpdateGroup;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 字典管理
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2019-11-15 11:03:14
 */
@Accessors(chain = true)
@Data
@TableName("sys_dictionary_manage")
public class SysDictionaryManageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 字典编号
     */
    @NotNull(message = "字典编号不能为空", groups = UpdateGroup.class)
    @TableId
    private Integer id;
    /**
     * 数据字典id
     */
    @NotNull(message = "数据字典id不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private Integer did;
    /**
     * 名称
     */
    @NotNull(message = "字典项名字不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String name;
    /**
     * 值
     */
    private String value;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDateTime deletedAt;

}
