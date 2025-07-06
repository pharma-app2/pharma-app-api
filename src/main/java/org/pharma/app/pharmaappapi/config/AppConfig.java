package org.pharma.app.pharmaappapi.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.record.RecordModule;
import org.modelmapper.record.RecordValueReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // modelMapper creates the instance object destination using a no-arguments constructor and populating the
        // fields after that, but a "record" doesn't have a no-arguments constructor.
        // With this strategy, modelMapper uses all-arguments constructor
//        modelMapper.getConfiguration().addValueReader(new RecordValueReader());

        return modelMapper;
    }
}
