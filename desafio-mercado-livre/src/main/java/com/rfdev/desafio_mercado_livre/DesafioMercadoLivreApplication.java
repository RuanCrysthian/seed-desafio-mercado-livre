package com.rfdev.desafio_mercado_livre;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DesafioMercadoLivreApplication {

	public static void main(String[] args) {
		SpringApplication.run(DesafioMercadoLivreApplication.class, args);
	}

}
