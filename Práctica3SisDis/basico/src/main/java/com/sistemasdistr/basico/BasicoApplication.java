package com.sistemasdistr.basico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Archivo principal de Spring Boot. 
// Contiene el método main() que se encarga de encender todo el servidor web de Java.
@SpringBootApplication
public class BasicoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasicoApplication.class, args);
	}

}