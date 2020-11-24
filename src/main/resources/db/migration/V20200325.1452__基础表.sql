SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_captcha
-- ----------------------------
DROP TABLE IF EXISTS `sys_captcha`;
CREATE TABLE `sys_captcha`
(
    `uuid`        char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci   NOT NULL COMMENT 'uuid',
    `code`        varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '验证码',
    `expire_time` datetime(0)                                                 NULL DEFAULT NULL COMMENT '过期时间',
    PRIMARY KEY (`uuid`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4 COMMENT = '系统验证码';

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`
(
    `id`          bigint(20)                                                     NOT NULL AUTO_INCREMENT,
    `param_key`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci   NULL DEFAULT NULL COMMENT 'key',
    `param_value` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'value',
    `status`      tinyint(4)                                                     NULL DEFAULT 1 COMMENT '状态   0：隐藏   1：显示',
    `remark`      varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `param_key` (`param_key`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4 COMMENT = '系统配置信息表';

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`
(
    `id`          bigint(20)                                                     NOT NULL AUTO_INCREMENT,
    `username`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci   NULL DEFAULT NULL COMMENT '用户名',
    `operation`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci   NULL DEFAULT NULL COMMENT '用户操作',
    `method`      varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL DEFAULT NULL COMMENT '请求方法',
    `params`      varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求参数',
    `time`        bigint(20)                                                     NOT NULL COMMENT '执行时长(毫秒)',
    `ip`          varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci   NULL DEFAULT NULL COMMENT 'IP地址',
    `create_date` datetime(0)                                                    NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4 COMMENT = '系统日志';

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`
(
    `menu_id`   bigint(20)                                                    NOT NULL AUTO_INCREMENT,
    `parent_id` bigint(20)                                                    NULL DEFAULT NULL COMMENT '父菜单ID，一级菜单为0',
    `name`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '菜单名称',
    `url`       varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '菜单URL',
    `perms`     varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '授权(多个用逗号分隔，如：user:list,user:create)',
    `type`      int(11)                                                       NULL DEFAULT NULL COMMENT '类型   0：目录   1：菜单   2：按钮',
    `icon`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL DEFAULT NULL COMMENT '菜单图标',
    `order_num` int(11)                                                       NULL DEFAULT NULL COMMENT '排序',
    PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4 COMMENT = '菜单管理';

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `role_id`        bigint(20)                                                    NOT NULL AUTO_INCREMENT,
    `role_name`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色名称',
    `remark`         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
    `create_user_id` bigint(20)                                                    NULL DEFAULT NULL COMMENT '创建者ID',
    `create_time`    datetime(0)                                                   NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4 COMMENT = '角色';

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`
(
    `id`      bigint(20) NOT NULL AUTO_INCREMENT,
    `role_id` bigint(20) NULL DEFAULT NULL COMMENT '角色ID',
    `menu_id` bigint(20) NULL DEFAULT NULL COMMENT '菜单ID',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4 COMMENT = '角色与菜单对应关系';

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `user_id`        bigint(20)                                                    NOT NULL AUTO_INCREMENT,
    `username`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL DEFAULT NULL,
    `password`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
    `salt`           varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL DEFAULT NULL COMMENT '盐',
    `email`          varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
    `mobile`         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
    `status`         tinyint(2)                                                    NULL DEFAULT NULL COMMENT '状态  0：禁用   1：正常',
    `create_user_id` bigint(20)                                                    NULL DEFAULT NULL COMMENT '创建者ID',
    `create_time`    datetime(0)                                                   NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`user_id`) USING BTREE,
    UNIQUE INDEX `username` (`username`) USING BTREE,
    UNIQUE KEY `mobile` (`mobile`) USING BTREE,
    UNIQUE KEY `email` (`email`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4 COMMENT = '系统用户';

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`
(
    `id`      bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id` bigint(20) NULL DEFAULT NULL COMMENT '系统用户ID',
    `role_id` bigint(20) NULL DEFAULT NULL COMMENT '角色ID',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4 COMMENT = '用户与角色对应关系';

-- ----------------------------
-- Table structure for sys_user_token
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_token`;
CREATE TABLE `sys_user_token`
(
    `user_id`     bigint(20)                                                    NOT NULL,
    `token`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'token',
    `expire_time` datetime(0)                                                   NULL DEFAULT NULL COMMENT '过期时间',
    `update_time` datetime(0)                                                   NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`user_id`) USING BTREE,
    UNIQUE INDEX `token` (`token`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4 COMMENT = '系统用户Token';


DROP TABLE IF EXISTS `sys_data_dictionary`;
CREATE TABLE `sys_data_dictionary`
(
    `id`         int(11)      NOT NULL AUTO_INCREMENT,
    `pid`        int(11)  DEFAULT NULL COMMENT '父节点id',
    `name`       varchar(255) NOT NULL COMMENT '名称',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARSET = utf8mb4 COMMENT ='数据字典';


DROP TABLE IF EXISTS `sys_dictionary_manage`;
CREATE TABLE `sys_dictionary_manage`
(
    `id`         int(11)      NOT NULL AUTO_INCREMENT COMMENT '字典编号',
    `did`        int(11)      DEFAULT NULL COMMENT '数据字典id',
    `name`       varchar(255) NOT NULL COMMENT '名称',
    `value`      varchar(255) DEFAULT NULL COMMENT '值',
    `created_at` datetime     DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at` datetime     DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_sys_dictionary_manage_did_name` (`did`, `name`)
) ENGINE = InnoDB
  CHARSET = utf8mb4 COMMENT ='字典管理';


DROP TABLE IF EXISTS `t_file`;
CREATE TABLE `t_file`
(
    `id`                int(11)      NOT NULL AUTO_INCREMENT,
    `original_filename` varchar(255) NULL DEFAULT NULL COMMENT '原始文件名',
    `path`              varchar(500) NULL DEFAULT NULL COMMENT '存储路径',
    `size`              bigint(20)   NULL DEFAULT NULL COMMENT '对象大小（字节）',
    `mime_type`         varchar(255) NULL DEFAULT NULL COMMENT 'MIME类型',
    `uploader_id`       bigint(20)   NULL DEFAULT NULL COMMENT '上传用户id',
    `is_crawl`          tinyint(2)   NULL DEFAULT 0 COMMENT '是否爬取',
    `created_at`        datetime(0)  NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        datetime(0)  NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
    `deleted_at`        datetime(0)  NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_t_file_path` (`path`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4 COMMENT = '文件对象表';


DROP TABLE IF EXISTS `seller`;
CREATE TABLE `seller`
(
    `id`            int(11)       NOT NULL AUTO_INCREMENT,
    `name`          varchar(80)   NOT NULL DEFAULT '' COMMENT '商家名称',
    `description`   varchar(255)  NOT NULL DEFAULT '' COMMENT '自我介绍',
    `created_at`    datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark_score`  decimal(2, 1) NOT NULL DEFAULT '0.0' COMMENT '商家评分',
    `disabled_flag` tinyint(2)    NOT NULL DEFAULT '0' COMMENT '是否禁用',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商家表';


-- CREATE TABLE IF NOT EXISTS `house`
-- (
--     `id`             int(11) unsigned NOT NULL AUTO_INCREMENT,
--     `shop_id`        int(11)                   DEFAULT NULL COMMENT '所属店铺id',
--     `title`          varchar(32)      NOT NULL COMMENT '房源名称',
--     `type`           int(11)          NOT NULL DEFAULT '0' COMMENT '0:民宿 1:经济型酒店 2:主题酒店 3:商务酒店 4:公寓 5:豪华酒店 6:客栈 7:青年旅社',
--     `price`          int(11) unsigned NOT NULL COMMENT '价格/天',
--     `area`           int(11) unsigned NOT NULL COMMENT '面积',
--     `room`           int(11) unsigned NOT NULL COMMENT '卧室数量',
--     `build_year`     int(4)           NOT NULL COMMENT '建立年份',
--     `status`         int(4) unsigned  NOT NULL DEFAULT '0' COMMENT '房源状态 0-未审核 1-审核通过 2-已出租 3-逻辑删除',
--     `created_at`     datetime         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--     `updated_at`     datetime         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
--     `city_en_name`   varchar(32)      NOT NULL COMMENT '城市标记缩写 如 北京bj',
--     `region_en_name` varchar(255)     NOT NULL COMMENT '地区英文简写 如昌平区 cpq',
--     `address`        varchar(255)     NOT NULL COMMENT '详细地址',
--     `cover`          varchar(255)              DEFAULT NULL COMMENT '封面',
--     `parlour`        int(11)          NOT NULL DEFAULT '0' COMMENT '客厅数量',
--     `bathroom`       int(11)          NOT NULL DEFAULT '0',
--     `admin_id`       int(11)          NOT NULL COMMENT '所属管理员id',
--     PRIMARY KEY (`id`)
-- ) ENGINE = InnoDB
--   DEFAULT CHARSET = utf8mb4 COMMENT ='房源信息表';


DROP TABLE IF EXISTS `area`;
CREATE TABLE `area`
(
    `id`         int(11)      NOT NULL AUTO_INCREMENT,
    `pid`        int(11)      NOT NULL DEFAULT 0 COMMENT '父级id（一级为0）',
    `name`       varchar(255) NOT NULL COMMENT '地区名',
    `level`      TINYINT(2)   NOT NULL COMMENT '0:省份/直辖市,1:市级单位,2:区级单位（直辖市在level=0能够找到，在level=1也能找到）',
    `created_at` datetime              DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_area_pid` (`pid`)
) COMMENT = '地区表';



DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop`
(
    `id`            int(11)       NOT NULL AUTO_INCREMENT,
    `title`         varchar(80)   NOT NULL DEFAULT '' COMMENT '商铺标题',
    `description`   varchar(1024) NOT NULL DEFAULT '' COMMENT '商铺介绍',
    `remark_score`  decimal(2, 1) NOT NULL DEFAULT '0.0' COMMENT '商铺评分',
    `price_per_man` int(11)       NOT NULL DEFAULT '0' COMMENT '人均消费',
    `province`      varchar(32)   NOT NULL COMMENT '省份/直辖市',
    `city`          varchar(32)   NOT NULL COMMENT '市级单位',
    `region`        varchar(255)  NOT NULL COMMENT '区级单位',
    `address`       varchar(255)  NOT NULL COMMENT '详细地址',
    `category_id`   int(11)                DEFAULT NULL COMMENT '商铺类别id',
    `tags`          varchar(2000) NOT NULL DEFAULT '' COMMENT '以" "分隔的标签',
    `created_at`    datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `seller_id`     int(11)       NOT NULL DEFAULT '0' COMMENT '商家id',
    `cover_file_id` int(11)                DEFAULT NULL COMMENT '封面',
    `disabled_flag` tinyint(2)    NOT NULL DEFAULT '1' COMMENT '是否禁用,默认禁用,需要手动上架',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='店铺表';


DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`
(
    `id`         int(11)      NOT NULL AUTO_INCREMENT,
    `name`       varchar(20)  NOT NULL DEFAULT '' COMMENT '商铺类别名称',
    `icon_url`   varchar(200) NOT NULL DEFAULT '' COMMENT '类别图标',
    `sort`       int(11)      NOT NULL DEFAULT '0' COMMENT '显示顺序',
    `created_at` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `name_unique_index` (`name`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商铺类别表';


# CREATE TABLE IF NOT EXISTS `commodity`
# (
#     `id`                    int(11)  NOT NULL AUTO_INCREMENT,
#     `name`                  varchar(50)       DEFAULT NULL COMMENT '商品名',
#     `price`                 decimal(10, 2)    DEFAULT NULL COMMENT '商品金额',
#     `commodity_category_id` int(11)           DEFAULT NULL COMMENT '商品分类id',
#     `create_user_id`        int(11)           DEFAULT NULL COMMENT '创建人（后台用户ID）',
#     `status`                int(4)            DEFAULT NULL COMMENT '状态（0:禁用 1:启用）',
#     `created_at`            datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
#     `updated_at`            datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
#     PRIMARY KEY (`id`)
# ) ENGINE = InnoDB
#   DEFAULT CHARSET = utf8mb4 COMMENT ='商品表';
#
#
# CREATE TABLE IF NOT EXISTS `commodity_category`
# (
#     `id`             int(11)  NOT NULL AUTO_INCREMENT,
#     `name`           varchar(50)       DEFAULT NULL COMMENT '类目ID',
#     `pid`            int(11)           DEFAULT NULL COMMENT '父类别ID（一级类目为0）',
#     `create_user_id` int(11)           DEFAULT NULL COMMENT '创建人（后台用户ID）',
#     `status`         int(4)            DEFAULT NULL COMMENT '状态（0:禁用 1:启用）',
#     `created_at`     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
#     `updated_at`     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
#     PRIMARY KEY (`id`)
# ) ENGINE = InnoDB
#   DEFAULT CHARSET = utf8mb4 COMMENT ='商品类别表';


DROP TABLE IF EXISTS `member_shop_like`;
CREATE TABLE `member_shop_like`
(
    `id`         int(11)  NOT NULL AUTO_INCREMENT,
    `member_id`  int(11)           DEFAULT NULL COMMENT '会员id',
    `shop_id`    int(11)           DEFAULT NULL COMMENT '店铺id',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='会员点赞店铺表';



DROP TABLE IF EXISTS `member`;
CREATE TABLE `member`
(
    `id`                 int(11)      NOT NULL AUTO_INCREMENT,
    `username`           varchar(25)  NOT NULL COMMENT '会员用户名',
    `password`           varchar(255) NOT NULL COMMENT '登录密码',
    `sex`                tinyint(2)            DEFAULT '-1' COMMENT '性别（-1:未知 1:男 2:女）',
    `salt`               varchar(255)          DEFAULT NULL COMMENT '盐',
    `email`              varchar(100) NOT NULL COMMENT '邮箱',
    `phone`              varchar(255)          DEFAULT NULL,
    `birth`              date                  DEFAULT NULL COMMENT '出生日期',
    `avatar`             int(11)               DEFAULT NULL COMMENT '头像',
    `address_default_id` int(11)               DEFAULT NULL COMMENT '默认地址ID',
    `member_channel`     int(4)                DEFAULT NULL COMMENT '会员注册渠道（1:IOS 2:android 3:微信小程序 4:微信公众号 5:h5）',
    `mp_open_id`         varchar(32)           DEFAULT NULL COMMENT '微信公众号openId',
    `status`             tinyint(2)   NOT NULL DEFAULT '1' COMMENT '状态（0:禁用 1:启用）',
    `created_at`         datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`         datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `member_uk1` (`username`),
    UNIQUE KEY `member_uk2` (`email`),
    UNIQUE KEY `member_uk3` (`phone`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='会员表';


DROP TABLE IF EXISTS `member_addr`;
CREATE TABLE `member_addr`
(
    `id`             int(11)  NOT NULL AUTO_INCREMENT,
    `member_id`      int(11)           DEFAULT NULL COMMENT '会员id',
    `contact_person` varchar(50)       DEFAULT NULL COMMENT '联系人',
    `contact_phone`  varchar(50)       DEFAULT NULL COMMENT '联系电话',
    `address`        varchar(255)      DEFAULT NULL COMMENT '地址',
    `created_at`     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback`
(
    `id`               int(11)  NOT NULL AUTO_INCREMENT,
    `member_id`        int(11)           DEFAULT NULL,
    `feedback_content` tinytext COMMENT '反馈内容',
    `feedback_type`    int(4)            DEFAULT NULL COMMENT '反馈类型（1:破损 2:缺货 3:错货 4:投诉）',
    `created_at`       datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`       datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='反馈表';

DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon`
(
    `id`             int(11)  NOT NULL AUTO_INCREMENT,
    `coupon_name`    varchar(50)       DEFAULT NULL COMMENT '券名称',
    `coupon_price`   decimal(10, 2)    DEFAULT NULL COMMENT '券面金额（用于抵扣订单金额）',
    `create_user_id` int(11)           DEFAULT NULL COMMENT '创建人（后台用户ID）',
    `created_at`     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='抵扣券';



DROP TABLE IF EXISTS `coupon_member`;
CREATE TABLE `coupon_member`
(
    `id`             int(11)  NOT NULL AUTO_INCREMENT,
    `coupon_id`      int(11)           DEFAULT NULL COMMENT '抵扣券id',
    `member_id`      int(11)           DEFAULT NULL COMMENT '会员id',
    `coupon_channel` int(4)            DEFAULT NULL COMMENT '领取券的渠道（1:平台发放 2:用户购买）',
    `created_at`     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='会员-抵扣券中间表';

DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order`
(
    `id`           bigint(20) NOT NULL COMMENT '订单ID',
    `member_id`    int(11)             DEFAULT NULL COMMENT '会员ID',
    `origin_price` decimal(10, 2)      DEFAULT NULL COMMENT '订单原价',
    `pay_price`    decimal(10, 2)      DEFAULT NULL COMMENT '订单实付',
#     `shop_id`      int(11)        DEFAULT NULL COMMENT '门店ID',
#     `shop_name`    varchar(50)    DEFAULT NULL COMMENT '门店名称',
    `status`       tinyint(2)          DEFAULT NULL COMMENT '订单状态（1:进行中 2:已完成 3:已取消）',
    `created_at`   datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`   datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='订单表';


DROP TABLE IF EXISTS `t_order_commodity`;
CREATE TABLE `t_order_commodity`
(
    `id`         int(11)    NOT NULL AUTO_INCREMENT,
    `order_id`   bigint(20) NOT NULL COMMENT '订单ID',
    `shop_id`    int(11)             DEFAULT NULL COMMENT '商品ID',
    `shop_name`  varchar(255)        DEFAULT NULL COMMENT '商品名称',
    `num`        int(4)              DEFAULT NULL COMMENT '商品数量',
    `price`      decimal(10, 2)      DEFAULT NULL COMMENT '商品金额',
    `created_at` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_t_order_commodity_order_id_commodity_id` (`order_id`, `shop_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1001
  DEFAULT CHARSET = utf8mb4 COMMENT ='订单详情表';


DROP TABLE IF EXISTS `coupon_order`;
CREATE TABLE `coupon_order`
(
    `id`         int(11)  NOT NULL AUTO_INCREMENT,
    `coupon_id`  int(11)           DEFAULT NULL COMMENT '抵扣券id',
    `member_id`  int(11)           DEFAULT NULL COMMENT '会员id',
    `order_id`   int(11)           DEFAULT NULL COMMENT '商品订单ID',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='订单-抵扣券';


DROP TABLE IF EXISTS `recommend`;
CREATE TABLE `recommend`
(
    `member_id` int(11) NOT NULL COMMENT '会员id',
    `shop_ids`  varchar(255) DEFAULT NULL COMMENT '以“,”分隔的推荐商铺id字符串数组',
    PRIMARY KEY (`member_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户-推荐商铺表';


DROP TABLE IF EXISTS `t_delivery`;
CREATE TABLE `t_delivery`
(
    `id`             int(11)  NOT NULL AUTO_INCREMENT,
    `delivery_no`    varchar(64)       DEFAULT NULL COMMENT '配送单号',
    `order_id`       int(11)           DEFAULT NULL,
    `shop_id`        int(11)           DEFAULT NULL,
    `sys_user_id`    int(11)           DEFAULT NULL COMMENT '配送员后台用户id',
    `pick_time`      datetime          DEFAULT NULL COMMENT '取餐时间',
    `arrive_time`    datetime          DEFAULT NULL COMMENT '送达时间',
    `member_id`      int(11)           DEFAULT NULL COMMENT '会员id',
    `member_addr_id` int(11)           DEFAULT NULL COMMENT '会员配送地址id',
    `created_at`     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 801
  DEFAULT CHARSET = utf8mb4 COMMENT ='配送信息表';

DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`
(
    `id`            int(11)                             NOT NULL AUTO_INCREMENT,
    `pid`           int(11)                             NULL     DEFAULT NULL COMMENT '一级子评论和二级子评论的pid都=顶层父评论的id',
    `user_id`       int(11)                             NULL     DEFAULT NULL,
    `reply_user_id` int(11)                             NULL COMMENT '二级子评论被回复者id,一级子评论为NULL',
    `shop_id`       int(11)                             NULL     DEFAULT NULL comment '店铺id',
    `content`       varchar(1024) CHARACTER SET utf8mb4 NOT NULL DEFAULT '',
    `created_at`    datetime(0)                         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `fk_pid` (`pid`) USING BTREE,
    INDEX `fk_comment_user` (`user_id`) USING BTREE,
    INDEX `fk_reply_user_id` (`reply_user_id`) USING BTREE,
    INDEX `fk_comment_shop_id` (`shop_id`) USING BTREE,
    CONSTRAINT `fk_comment_shop_id` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
    CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `member` (`id`) ON DELETE SET NULL ON UPDATE NO ACTION,
    CONSTRAINT `fk_reply_user_id` FOREIGN KEY (`reply_user_id`) REFERENCES `member` (`id`) ON DELETE SET NULL ON UPDATE NO ACTION,
    CONSTRAINT `fk_pid` FOREIGN KEY (`pid`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT '评论表';

DROP TABLE IF EXISTS `comment_vote`;
CREATE TABLE `comment_vote`
(
    `user_id`    int(11) NOT NULL,
    `comment_id` int(11) NOT NULL,
    PRIMARY KEY (`user_id`, `comment_id`) USING BTREE,
    INDEX `fk_comment_vote_comment` (`comment_id`) USING BTREE,
    CONSTRAINT `fk_comment_vote_comment` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
    CONSTRAINT `fk_comment_vote_user` FOREIGN KEY (`user_id`) REFERENCES `member` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT '评论点赞表';


SET FOREIGN_KEY_CHECKS = 1;
