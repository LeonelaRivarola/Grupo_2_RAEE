package com.ayds2.Proyecto.ayds2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProyectoAyds2Application {

	public static void main(String[] args) {
		System.setProperty("server.port", "8081");
		SpringApplication.run(ProyectoAyds2Application.class, args);
	}

}
