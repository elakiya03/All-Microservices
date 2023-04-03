package com.app.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class AllMicroservicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(AllMicroservicesApplication.class, args);
	}
	
	@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/admin/all")
                        .allowedOrigins("http://localhost:3000") // replace with your frontend URL
                        .allowedMethods("GET", "POST", "PUT", "DELETE");
                        //.allowedHeaders("*");
            }
        };
	}

}
