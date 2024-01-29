package br.com.alura.logs;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;

@SpringBootApplication
public class CursoApplication {

	private static Logger logger = LoggerFactory.getLogger(CursoApplication.class);

	public static void main(String[] args) {
		logger.info("Iniciando a API de cursos Alura!");
		SpringApplication.run(CursoApplication.class, args);
		logger.info("API para cadastro de cursos na plataforma Alura!");
	}
}
