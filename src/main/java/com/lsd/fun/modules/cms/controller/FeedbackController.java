package com.lsd.fun.modules.cms.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.entity.FeedbackEntity;
import com.lsd.fun.modules.cms.service.FeedbackService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;



/**
 * 反馈表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@RestController
@RequestMapping("cms/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("cms:feedback:list")
    public R list(BaseQuery query){
        PageUtils page = feedbackService.queryPage(query);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("cms:feedback:info")
    public R info(@PathVariable("id") Integer id){
		FeedbackEntity feedback = feedbackService.getById(id);

        return R.ok().put("feedback", feedback);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("cms:feedback:save")
    public R save(@RequestBody FeedbackEntity feedback){
		feedbackService.save(feedback);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("cms:feedback:update")
    public R update(@RequestBody FeedbackEntity feedback){
		feedbackService.updateById(feedback);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("cms:feedback:delete")
    public R delete(@RequestBody Integer[] ids){
		feedbackService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
