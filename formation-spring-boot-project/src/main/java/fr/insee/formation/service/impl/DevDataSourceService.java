package fr.insee.formation.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import fr.insee.formation.service.DataSourceService;

@Service
@Profile("dev")
public class DevDataSourceService implements DataSourceService {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Override
    public String getInfo() {
        return "Using DEVELOPMENT in-memory H2 database (dev profile)";
    }

    @Override
    public String getDbUrl() {
        return dbUrl;
    }
}