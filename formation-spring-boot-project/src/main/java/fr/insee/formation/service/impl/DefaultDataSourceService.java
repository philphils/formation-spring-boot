package fr.insee.formation.service.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import fr.insee.formation.service.DataSourceService;

@Service
@Profile("!dev & !integration")
public class DefaultDataSourceService implements DataSourceService {
    @Override
    public String getInfo() {
        return "Using DEFAULT : no specific database configuration (default profile)";
    }
}