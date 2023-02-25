package com.hubert.glevia2accountcreator.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "recaptcha")
public class RecaptchaConfiguration {
    private String siteKey;
    private String apiKey;
    private String action;
}
