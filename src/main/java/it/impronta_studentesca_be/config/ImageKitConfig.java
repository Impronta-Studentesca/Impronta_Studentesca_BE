package it.impronta_studentesca_be.config;

import io.imagekit.sdk.ImageKit;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ImageKitConfig {

    @Value("${imagekit.public-key}")
    private String publicKey;

    @Value("${imagekit.private-key}")
    private String privateKey;

    @Value("${imagekit.url-endpoint}")
    private String urlEndpoint;

    @Bean
    public ImageKit imageKit() {
        // Uso il nome completo per evitare conflitto con @Configuration di Spring
        io.imagekit.sdk.config.Configuration config =
                new io.imagekit.sdk.config.Configuration(publicKey, privateKey, urlEndpoint);

        ImageKit imageKit = ImageKit.getInstance();
        imageKit.setConfig(config);
        return imageKit;
    }

    @PostConstruct
    void logConfig() {
        log.info("ImageKit configured. urlEndpoint={}, publicKeyPrefix={}",
                urlEndpoint,
                publicKey != null && publicKey.length() >= 6 ? publicKey.substring(0, 6) + "***" : "null");
    }
}