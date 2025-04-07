package com.justintime.jit.config;

import com.justintime.jit.components.RestaurantIdResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RestaurantIdResolver restaurantIdResolver;

    public WebConfig (RestaurantIdResolver restaurantIdResolver) {
        this.restaurantIdResolver = restaurantIdResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolverList) {
        resolverList.add(restaurantIdResolver);
    }
}
