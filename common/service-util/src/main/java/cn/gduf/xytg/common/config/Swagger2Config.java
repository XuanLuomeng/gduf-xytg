package cn.gduf.xytg.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description Swagger2配置信息
 * @date 2025/10/17 22:12
 */
@Configuration
@EnableSwagger2WebMvc
public class Swagger2Config {
    @Bean
    public Docket webApiConfig() {
        // 添加全局参数
        List<Parameter> parameters = new ArrayList<>();
        // 创建 ParameterBuilder
        ParameterBuilder parameterBuilder = new ParameterBuilder();

        // 构建 ParameterBuilder
        parameterBuilder.name("userId")
                .description("用户token")
                .defaultValue("1")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false)
                .build();

        // 将 ParameterBuilder 添加到 parameters 中
        parameters.add(parameterBuilder.build());

        // 创建 Docket 对象
        Docket webApi = new Docket(DocumentationType.SWAGGER_2)
                .groupName("webApi")
                .apiInfo(webApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.gduf.xytg"))
                .paths(PathSelectors.regex("/api/.*"))
                .build()
                .globalOperationParameters(parameters);

        return webApi;
    }

    @Bean
    public Docket adminApiConfig() {
        // 添加全局参数
        List<Parameter> parameters = new ArrayList<>();
        // 创建 ParameterBuilder
        ParameterBuilder parameterBuilder = new ParameterBuilder();

        // 构建 ParameterBuilder
        parameterBuilder.name("adminId")
                .description("用户token")
                .defaultValue("1")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false)
                .build();

        // 将 ParameterBuilder 添加到 parameters 中
        parameters.add(parameterBuilder.build());

        // 创建 Docket 对象
        Docket adminApi = new Docket(DocumentationType.SWAGGER_2)
                .groupName("adminApi")
                .apiInfo(adminApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.gduf.xytg"))
                .paths(PathSelectors.regex("/admin/.*"))
                .build()
                .globalOperationParameters(parameters);

        return adminApi;
    }

    private ApiInfo webApiInfo() {
        // 创建 ApiInfo 对象
        return new ApiInfoBuilder()
                .title("Xytg-网站-API文档")
                .description("广东金融学院-校园团购-微服务接口定义文档")
                .version("1.0")
                .contact(new Contact("LuoXuanwei", "http://gduf.cn", "LuoXuanwei"))
                .build();
    }

    private ApiInfo adminApiInfo() {
        // 创建 ApiInfo 对象
        return new ApiInfoBuilder()
                .title("Xytg-后台管理系统-API文档")
                .description("广东金融学院-校园团购-后台管理系统接口定义文档")
                .version("1.0")
                .contact(new Contact("LuoXuanwei", "http://gduf.cn", "LuoXuanwei"))
                .build();
    }
}
