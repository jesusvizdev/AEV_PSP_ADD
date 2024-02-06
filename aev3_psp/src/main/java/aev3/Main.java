package aev3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal que inicia la aplicación Spring Boot.
 */
@SpringBootApplication
public class Main {

	/**
	 * Método principal que inicia la aplicación Spring Boot.
	 *
	 * @param args Argumentos de línea de comandos (pueden ser utilizados para la
	 *             configuración).
	 */
	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}
