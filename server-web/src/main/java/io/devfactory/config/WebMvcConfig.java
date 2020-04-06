package io.devfactory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("views/index");
        registry.addViewController("/sign-in/form").setViewName("views/sign/signIn");
        registry.addViewController("/sign-up/form").setViewName("views/sign/signUp");
    }

}
