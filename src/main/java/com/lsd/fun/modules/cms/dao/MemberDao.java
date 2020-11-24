package com.lsd.fun.modules.cms.dao;

import com.lsd.fun.modules.app.vo.MemberVo;
import com.lsd.fun.modules.cms.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {

    MemberVo queryById(Integer userId);
}
