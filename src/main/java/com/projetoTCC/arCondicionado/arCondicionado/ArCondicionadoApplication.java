package com.projetoTCC.arCondicionado.arCondicionado;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ArCondicionadoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArCondicionadoApplication.class, args);
	}

}
