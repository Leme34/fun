package com.lsd.fun.modules.sys.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;
import com.lsd.fun.modules.sys.query.SysLogQuery;
import com.lsd.fun.modules.sys.service.SysLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 系统日志
 *
 */
@Api(tags = "系统日志相关")
@Controller
@RequestMapping("/sys/log")
public class SysLogController {
	@Autowired
	private SysLogService sysLogService;

	/**
	 * 列表
	 */
	@ApiOperation("查询所有")
	@ResponseBody
	@GetMapping("/list")
	@RequiresPermissions("sys:log:list")
	public R list(SysLogQuery queryForm){
		PageUtils page = sysLogService.queryPage(queryForm);

		return R.ok().put("page", page);
	}

}
