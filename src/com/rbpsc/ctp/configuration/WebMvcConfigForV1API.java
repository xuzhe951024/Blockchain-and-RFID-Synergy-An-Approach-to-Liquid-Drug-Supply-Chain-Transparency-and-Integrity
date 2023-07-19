package com.rbpsc.ctp.configuration;


import com.rbpsc.ctp.common.Constant.ServiceConstants;
import com.rbpsc.ctp.configuration.v1prefix.V1RestController;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfigForV1API implements WebMvcConfigurer {
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer){
        configurer.addPathPrefix(ServiceConstants.V1_BASE_ADD, c -> c.isAnnotationPresent(V1RestController.class));
    }
}