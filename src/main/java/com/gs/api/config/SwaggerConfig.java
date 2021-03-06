package com.gs.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket docker() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gs.api.api.resource"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(info());
    }

    private ApiInfo info() {
        return new ApiInfoBuilder()
                .title("Library API")
                .description("A Library API")
                .contact(contact())
                .version("1.0")
                .build();
    }

    private Contact contact() {
        return new Contact(
                "Guilherme",
                "https://github.com/guilhermestella",
                "eng.guilhermestella@gmail.com");
    }
}
