package de.wirvsvirus.hack.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class PropertyConfiguration {
  @Bean
  public PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
    final PropertySourcesPlaceholderConfigurer propsConfig =
        new PropertySourcesPlaceholderConfigurer();
    propsConfig.setLocation(new ClassPathResource("git.properties"));

    return propsConfig;
  }
}
