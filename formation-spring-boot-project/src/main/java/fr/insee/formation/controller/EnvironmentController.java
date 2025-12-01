package fr.insee.formation.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class EnvironmentController {
    @Value("${app.environment}")
    private String environment;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @GetMapping("/environment")
    public Map<String, Object> getEnvironment() {
        Map<String, Object> result = new HashMap<>();
        result.put("environment", environment);
        result.put("databaseUrl", dbUrl);
        return result;
    }
}