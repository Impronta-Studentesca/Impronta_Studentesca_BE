package it.impronta_studentesca_be.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.util.Base64;
import java.util.List;

@Configuration
public class GoogleDriveConfig {

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Value("${google.drive.application-name}")
    private String applicationName;

    @Value("${google.drive.oauth.client-id}")
    private String clientId;

    @Value("${google.drive.oauth.client-secret}")
    private String clientSecret;

    @Value("${google.drive.oauth.refresh-token}")
    private String refreshToken;

    @Bean
    public Drive drive() throws Exception {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        GoogleCredentials credentials = UserCredentials.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(refreshToken)
                .build()
                .createScoped(List.of(DriveScopes.DRIVE));

        return new Drive.Builder(httpTransport, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(applicationName)
                .build();
    }

}
