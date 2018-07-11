package cn.goudan.wang.passport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@ComponentScan(basePackages = {"cn.goudan.wang.passport"})
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WangApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(WangApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WangApplication.class);
    }
}
