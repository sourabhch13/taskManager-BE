package com.TaskManager.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsAllowing implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry reg){
        reg.addMapping("/**").allowedOrigins("*").allowedMethods("GET","POST","PUT","DELETE");
    }
}
