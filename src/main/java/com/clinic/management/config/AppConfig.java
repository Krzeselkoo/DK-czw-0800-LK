package com.clinic.management.config;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AppConfig {
    @Value("${app.visit.duration}")
    private int visitDuration;
}