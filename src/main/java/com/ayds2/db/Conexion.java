package com.ayds2.db;

import org.sql2o.Sql2o;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Conexion {

    @Bean
    public Sql2o sql2o() {
        return new Sql2o("jdbc:mysql://localhost:3306/raee", "root", "");
    }
}
