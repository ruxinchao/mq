package com.rxc.rocketmq.config;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import com.github.xiaoymin.knife4j.core.util.CollectionUtils;

import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @ClassName SwaggerConfig
 * @Version 1.0
 */
@Configuration
@EnableOpenApi
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig
{   
    @Bean
    @Order(value = 1)
    public Docket groupRestApiOrg()
    {
        Predicate<RequestHandler> selector1 = RequestHandlerSelectors.basePackage("com.rxc.rocketmq");
        return new Docket(DocumentationType.OAS_30).groupName("rxcDemo")
                .apiInfo(apiInfo())
                .select()
                .apis(selector1)// 扫描该包下的所有需要在Swagger中展示的API，@ApiIgnore注解标注的除外
                .paths(PathSelectors.any())
                .build().securityContexts(CollectionUtils.newArrayList(securityContext())).securitySchemes(CollectionUtils.<SecurityScheme>newArrayList(apiKey()));
    }
    
   
    private ApiInfo apiInfo()
    {
        return new ApiInfoBuilder().title("rocketmqDemo").description("rocketmqDemo接口文档")
                .contact(new Contact("rxc", "", "")).version("1.0.1").build();
    }

    private ApiKey apiKey() {
        return new ApiKey("token", "token", "header");
    }
 
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/.*"))
                .build();
    }
    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return CollectionUtils.newArrayList(new SecurityReference("token", authorizationScopes));
    }

 
}