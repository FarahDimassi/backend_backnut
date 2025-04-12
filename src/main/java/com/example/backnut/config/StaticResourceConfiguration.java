package com.example.backnut.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class StaticResourceConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Utiliser toUri() pour obtenir le chemin correct avec le préfixe "file:"
        String resourceLocation = Paths.get("C:/Users/farah/Downloads/backnut/backnut/uploads/").toUri().toString();
        System.out.println("Mapping '/uploads/**' vers : " + resourceLocation);
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation)
                .setCachePeriod(3600);
    }

}
