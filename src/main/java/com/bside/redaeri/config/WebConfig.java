package com.bside.redaeri.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bside.redaeri.login.LoginIdxArgumentResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final LoginIdxArgumentResolver userIdArgumentResolver;

    public WebConfig(LoginIdxArgumentResolver userIdArgumentResolver) {
        this.userIdArgumentResolver = userIdArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userIdArgumentResolver);
    }
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
            	registry.addMapping("/**")
                	.allowedOrigins("https://redaeri.vercel.app", "http://localhost:5173") // kakao 추가하면 등록
                	.allowedMethods("GET", "POST", "PATCH", "DELETE")
                	.allowedHeaders("*");
            }
        };
    }
}
