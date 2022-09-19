package com.lx.wxpush;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement
@MapperScan(basePackages = {"com.lx.wxpush.dao"})
public class WxPushApplication {

    public static void main(String[] args) {
        SpringApplication.run(WxPushApplication.class, args);
    }

}
