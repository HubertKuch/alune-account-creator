package com.hubert.aluneaccountcreator.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "imap")
public class ImapConfiguration {
    private String host;
    private boolean ssl;
}

