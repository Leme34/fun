package com.lsd.fun.modules.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.modules.cms.dao.AreaDao;
import com.lsd.fun.modules.cms.entity.AreaEntity;
import com.lsd.fun.modules.cms.query.AreaQuery;
import com.lsd.fun.modules.cms.service.AreaService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class AreaServiceImpl extends ServiceImpl<AreaDao, AreaEntity> implements AreaService {

    @Override
    public PageUtils queryPage(BaseQuery query) {
        IPage<AreaEntity> page = this.page(
                new Query<AreaEntity>().getPage(query),
                new QueryWrapper<AreaEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<AreaEntity> listTree() {
        List<AreaEntity> areas = this.list();
        if (CollectionUtils.isEmpty(areas)) {
            return areas;
        }
        Map<Integer, List<AreaEntity>> groupByPid = areas.stream()
                .filter(area -> area.getLevel() > 0)
                .collect(Collectors.groupingBy(AreaEntity::getPid));
        return areas.stream()
                .map(area -> {
                    List<AreaEntity> subAreas = groupByPid.get(area.getId());
                    return area.setSubAreas(subAreas);
                })
                .filter(area -> area.getLevel() == 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<AreaEntity> listSubArea(AreaQuery query) {
        // 使用父级地区名查询
        if (StringUtils.isNotBlank(query.getPName())) {
            Integer pid = this.lambdaQuery()
                    .select(AreaEntity::getId)
                    .eq(AreaEntity::getName, query.getPName())
                    .oneOpt()
                    .map(AreaEntity::getId)
                    .orElseThrow(() -> new RRException("暂不支持当前地区", HttpStatus.SC_NOT_FOUND));
            query.setPid(pid);
        }
        return this.lambdaQuery()
                .eq(query.getPid() != null, AreaEntity::getPid, query.getPid())
                .eq(query.getLevel() != null, AreaEntity::getLevel, query.getLevel())
                .list();
    }


}
