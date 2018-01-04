package com.dexmohq.blockchain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

//@EnableWebMvc
//@Configuration
//@ComponentScan(basePackageClasses = WebMvcConfig.class)
public class WebMvcConfig extends WebMvcConfigurerAdapter {

//    @Override
//    public void configureViewResolvers(ViewResolverRegistry registry) {
//        final InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
//        viewResolver.setPrefix("/resources/static/");
//        viewResolver.setSuffix(".html");
//        registry.viewResolver(viewResolver);
//    }

//    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/resources/static");
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("").setViewName("index");
//        registry.addViewController("/login").setViewName("login");
    }
}
