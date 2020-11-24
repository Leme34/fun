package com.lsd.fun.common.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.lsd.fun.modules.app.dto.UserRoleDto;

/**
 * 常量
 */
public class Constant {

    /**
     * 当前页码
     */
    public static final String PAGE = "page";
    /**
     * 每页显示记录数
     */
    public static final String LIMIT = "limit";
    /**
     * 排序字段
     */
    public static final String ORDER_FIELD = "sidx";
    /**
     * 排序方式
     */
    public static final String ORDER = "order";
    /**
     *  升序
     */
    public static final String ASC = "asc";
    /**
     * 0
     */
    public static final Integer FALSE = 0;
    /**
     * 1
     */
    public static final Integer TRUE = 1;


    public static final String ORDER_TYPE_ERROR = "出入库单类型错误";

    /**
     * 菜单类型
     */
    @Getter
    @AllArgsConstructor
    public enum MenuType {
        /**
         * 目录
         */
        CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮
         */
        BUTTON(2);

        private int value;
    }

    /**
     * 定时任务状态
     */
    @Getter
    @AllArgsConstructor
    public enum ScheduleStatus {
        /**
         * 正常
         */
        NORMAL(0),
        /**
         * 暂停
         */
        PAUSE(1);

        private int value;

    }


    /**
     * 系统角色名称枚举类
     */
    @Getter
    @AllArgsConstructor
    public enum RoleName {
        /** 超级管理员role名称 */
        SUPER_ADMIN_ROLENAME("超级管理员"),
        ;

        private String roleName;
    }


}
