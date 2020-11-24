package com.lsd.fun.modules.itag;

import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.common.utils.R;
import com.lsd.fun.modules.app.dto.TagDto;
import com.lsd.fun.modules.app.vo.MemberTag;
import com.lsd.fun.modules.itag.data_graphics_etl.impl.*;
import com.lsd.fun.modules.itag.dto.ETLTaskResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by lsd
 * 2020-03-06 20:49
 */
@Api(tags = "数据清洗")
@Slf4j
@RequestMapping("etl")
@Controller
public class EsQueryController {

    @Autowired
    private ETLEsService etlEsService;
    @Autowired
    private UserHeatETL userHeatETL;
    @Autowired
    private FunnelETL funnelETL;
    @Autowired
    private WeekOnWeekETL weekOnWeekETL;
    @Autowired
    private LineChartETL lineChartETL;
    @Autowired
    private RemindETL remindETL;
    @Autowired
    private MemberTagETL memberTagETL;

    // fix线程池,改为同步队列，保证只有一次并发请求能成功
    private static final int TASK_NUM = 6;
    private final static ExecutorService threadPool = new ThreadPoolExecutor(
            TASK_NUM, TASK_NUM,
            0L, TimeUnit.MILLISECONDS,
            new SynchronousQueue<>(),
            (r, executor) -> {
                log.error("Task " + r.toString() + " rejected from " + executor.toString());
                throw new RRException("系统正在努力计算，请稍后再试");
            }
    );

    /**
     * 同步执行耗时：系统启动第一次执行1.6min,之后再次执行38.174s  36.599s
     */
    @ApiOperation(value = "同步执行数据清洗任务", notes = "数据大盘图标数据存入Redis,会员用户数据清洗并存入ES中")
    @GetMapping("/exec-sync")
    @ResponseBody
    public R etlAndStoreSync() {
        log.debug("开始同步执行数据清洗任务");
        final long start = System.currentTimeMillis();
        memberTagETL.cache();
        userHeatETL.cache();
        funnelETL.cache();
        weekOnWeekETL.cache();
        lineChartETL.cache();
        remindETL.cache();
        log.debug("同步数据清洗任务完成，总耗时：{}s", (System.currentTimeMillis() - start) / 1000.0);
        return R.ok();
    }


    /**
     * 异步执行耗时：系统启动第一次执行1.5min,之后再次执行29.549s  27.834s
     */
    @ApiOperation(value = "异步执行数据清洗任务", notes = "数据大盘图标数据存入Redis,会员用户数据清洗并存入ES中")
    @GetMapping("/exec-async")
    @ResponseBody
    public R etlAndStoreAsync() {
        List<ETLTaskResult> resultList = new ArrayList<>();

        CompletionService<ETLTaskResult> completionService = new ExecutorCompletionService<>(threadPool);
        log.debug("开始异步执行数据清洗任务");
        final long start = System.currentTimeMillis();
        completionService.submit(() -> memberTagETL.cache());
        completionService.submit(() -> userHeatETL.cache());
        completionService.submit(() -> funnelETL.cache());
        completionService.submit(() -> weekOnWeekETL.cache());
        completionService.submit(() -> lineChartETL.cache());
        completionService.submit(() -> remindETL.cache());
        try {
            for (int i = 0; i < TASK_NUM; i++) {
                Future<ETLTaskResult> future = completionService.take();
                ETLTaskResult result = future.get();
                log.debug(result.getTaskName() + "完成");
                resultList.add(result);
            }
        } catch (InterruptedException e) {
            // 因为任务已经取消无需返回结果，所以恢复中断
            log.error("数据清洗任务中断", e);
            return R.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "数据清洗任务中断");
        } catch (ExecutionException e) {
            log.error("数据清洗任务异常", e);
            return R.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "数据清洗任务异常");
        }
        log.debug("异步数据清洗任务完成，总耗时：{}s", (System.currentTimeMillis() - start) / 1000.0);
        return R.ok().put("data", resultList);
    }


    @ApiOperation(value = "ETL数据大盘结果")
    @GetMapping("/graphics")
    @ResponseBody
    public R graphics() {
        Map<String, Object> userHeatETLResult = userHeatETL.query();
        Map<String, Object> funnelETLResult = funnelETL.query();
        Map<String, Object> weekOnWeekETLResult = weekOnWeekETL.query();
        Map<String, Object> lineChartETLResult = lineChartETL.query();
        Map<String, Object> remindETLResult = remindETL.query();
        return R.ok()
                .put(userHeatETLResult)
                .put(funnelETLResult)
                .put(weekOnWeekETLResult)
                .put(lineChartETLResult)
                .put(remindETLResult);
    }


    @ApiOperation("查询并生成文本文件")
    @PostMapping("/gen")
    public void queryAndGen(@RequestBody List<TagDto> tagList, HttpServletResponse response) {
        // 查询es
        List<MemberTag> memberTags = etlEsService.query(tagList);
        String txtFileContent = totxtFileContent(memberTags);
        String name = "会员-手机号表格.csv";
        try (
                OutputStream os = response.getOutputStream();
                BufferedOutputStream bos = new BufferedOutputStream(os);
        ) {
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(name, "UTF-8"));
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);   // 网页直接下载
            response.setCharacterEncoding("UTF-8");
            response.addHeader("pragma", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            bos.write(txtFileContent.getBytes(StandardCharsets.UTF_8));
            bos.flush();
            response.flushBuffer();
        } catch (Exception e) {
            log.error("导出csv文件失败", e);
        }
    }


    /**
     * 结果转为文本，把 List<MemberTag> -> String
     */
    private String totxtFileContent(List<MemberTag> memberTags) {
        StringBuilder sb = new StringBuilder("会员ID,联系电话\r\n");
        for (MemberTag tag : memberTags) {
            sb.append(tag.getMemberId())
                    .append(",")
                    .append(tag.getPhone())
                    .append("\r\n");
        }
        return sb.toString();
    }


    @PreDestroy
    public void close() {
        threadPool.shutdown();
    }

}
