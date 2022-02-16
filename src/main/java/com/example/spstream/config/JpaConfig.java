package com.example.spstream.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("run")
public class JpaConfig {
    @Value("${DB_URL}")
    String url;
    @Value("${DB_USERNAME}")
    String username;
    @Value("${DB_PASSWORD}")
    String password;

    @Bean
    public DataSource getDataSource(){
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password).build();

    }
}
