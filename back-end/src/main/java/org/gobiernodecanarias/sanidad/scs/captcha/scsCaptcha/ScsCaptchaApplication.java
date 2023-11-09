package org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ScsCaptchaApplication {

    @Autowired
    private Environment env;

    public static void main(String[] args) {
        SpringApplication.run(ScsCaptchaApplication.class, args);
    }

    @Bean
    public CommandLineRunner clienteApiRest() {
        return arg -> {
            System.out.println("\n=======================================================");
            System.out.println("  API Rest del proyecto scsCaptcha v." + env.getProperty("numeroVersion"));
            System.out.println("=======================================================\n");
            System.out.println("Ready to comply ...\n");
        };
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*")
                        .allowedHeaders("*")
                        .allowedMethods("*")
                        .maxAge(1800L)
                        .allowCredentials(true)
                        .exposedHeaders("*");
            }
        };
    }
}
