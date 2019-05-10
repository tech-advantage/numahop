package fr.progilone.pgcn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class JaxbConfiguration {

    @Bean
    public Jaxb2Marshaller getMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("fr.progilone");
        return marshaller;
    }

}
