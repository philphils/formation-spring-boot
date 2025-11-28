package fr.insee.formation.service.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import fr.insee.formation.service.DataSourceService;

@Service
@Profile("integration")
public class IntegrationDataSourceService implements DataSourceService {
    @Override
    public String getInfo() {
        return "Using INTEGRATION H2 database (integration profile)";
    }
}