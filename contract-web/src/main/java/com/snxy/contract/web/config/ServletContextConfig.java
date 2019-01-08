package com.snxy.contract.web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * Created by zcb on 2017/5/16.
 */
@Configuration
@Slf4j
public class ServletContextConfig extends WebMvcConfigurationSupport {


//    @Autowired
//    private  SystemUserInterceptor systemUserInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        System.out.println("配置拦截器映射");
        //  registry.addResourceHandler("/").addResourceLocations("/**");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }

    //    @Bean
//    public LoginInterceptor getLoginInterceptor() {
//        return new LoginInterceptor();
//    }
//
//    @Bean
//    @ConfigurationProperties(prefix = "business")
//    public SystemUserInterceptor getSystemUserInterceptor() {
//
//        return new SystemUserInterceptor();
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        System.out.println("配置拦截器");
//        SystemUserInterceptor systemUserInterceptor = getSystemUserInterceptor();
//        registry.addInterceptor( systemUserInterceptor)//getSystemUserInterceptor())
//                .addPathPatterns("/**")
//                .excludePathPatterns("/file/image")
//                .excludePathPatterns("/register/password/update")
//                .excludePathPatterns("/register/getSmsCode")
//                .excludePathPatterns("/del/user");


    }

    @Override
    protected void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                .maxAge(3600);

    }

}
