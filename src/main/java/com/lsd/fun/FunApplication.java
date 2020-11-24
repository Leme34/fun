package com.lsd.fun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * Created by lsd
 * 2019-11-15 11:37
 */
@EnableAsync
@SpringBootApplication
public class FunApplication {
    public static void main(String[] args) {
        SpringApplication.run(FunApplication.class, args);
    }

    @PostConstruct
    void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }

}
