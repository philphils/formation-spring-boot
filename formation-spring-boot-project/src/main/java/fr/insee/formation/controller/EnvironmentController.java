package fr.insee.formation.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class EnvironmentController {

    @Value("${app.name}")
    private String name;

    @Value("${app.version}")
    private String version;

    @Value("${app.environment}")
    private String environment;

    @GetMapping("/environment")
    public Map<String, Object> getEnvironment() {
        Map<String, Object> result = new HashMap<>();
        result.put("Application name", name);
        result.put("Application version", version);
        result.put("environment", environment);
        return result;
    }
}