package com.concurrency.ecommerce.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "e-commerce API", description = "이커머스 서버 API" ))
public class SwaggerConfig {

}