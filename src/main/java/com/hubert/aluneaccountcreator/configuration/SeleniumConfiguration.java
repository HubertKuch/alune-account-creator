package com.hubert.aluneaccountcreator.configuration;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "selenium")
public class SeleniumConfiguration {
    @Value("${selenium.driver}")
    private String driver;

    @PostConstruct
    public void init() {
        System.setProperty("webdriver.chrome.driver", driver);
    }
}
