package com.revconnect.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Serve uploaded files from the external "uploads/" folder
     * (relative to the app's working directory) at the /uploads/** URL path.
     *
     * Files are saved here at runtime so they survive rebuilds and are
     * accessible immediately without restarting or recompiling.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Paths.get("uploads").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath + "/");
    }
}
