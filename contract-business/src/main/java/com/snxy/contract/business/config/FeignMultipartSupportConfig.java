package com.snxy.contract.business.config;


import feign.codec.Encoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhuchangbin
 * @date 2017/11/3
 */
@Configuration
public class FeignMultipartSupportConfig {

    @Bean
    public Encoder multipartFormEncoder() {
        return new FeignMultipartEncoder();
    }

    @Bean
    public feign.Logger.Level multipartLoggerLevel() {
        return feign.Logger.Level.FULL;
    }
}
