package com.lsd.fun.common.utils.excel.writer;

import com.lsd.fun.common.exception.RRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lsd
 * 2019-10-12 11:37
 */
@Component
public class ExcelWriterFactory {

    @Autowired
    Map<String, ExcelWriter> excelWriters = new ConcurrentHashMap<>();

    public ExcelWriter getWriter(String beanName) {
        final ExcelWriter excelWriter = excelWriters.get(beanName);
        if (null == excelWriter) {
            throw new RRException("【服务器错误】：not found handler for beanName:" + beanName);
        }
        return excelWriter;
    }

}
