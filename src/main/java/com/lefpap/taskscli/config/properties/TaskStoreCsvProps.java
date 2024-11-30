package com.lefpap.taskscli.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Profile("csv")
@ConfigurationProperties("task.store.csv")
public record TaskStoreCsvProps(
    String filepath,
    String delimiter
) { }
