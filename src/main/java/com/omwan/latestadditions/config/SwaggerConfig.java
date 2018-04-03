package com.omwan.latestadditions.config;

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

/**
 * Configurations for Swagger UI.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * Build API bean with path selectors.
     *
     * @return API object.
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/api/**"))
                .build()
                .apiInfo(buildApiInfo());
    }

    /**
     * Build api info object containing project information.
     *
     * @return api info object
     */
    private ApiInfo buildApiInfo() {
        return new ApiInfoBuilder()
                .contact(new Contact("Olivia Wan", "https://github.com/omwan", "wan.o@husky.neu.edu"))
                .description("Auto-generate playlist of latest additions to a set of playlists")
                .title("Latest Additions")
                .build();
    }
}
