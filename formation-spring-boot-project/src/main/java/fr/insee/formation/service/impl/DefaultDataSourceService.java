package fr.insee.formation.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import fr.insee.formation.service.DataSourceService;

@Service
@Profile("!dev & !integration")
public class DefaultDataSourceService implements DataSourceService {
    
    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Override
    public String getInfo() {
        return "Using DEFAULT : no specific database configuration (default profile)";
    }

    public String getDbUrl() {
        return dbUrl;
    }


}