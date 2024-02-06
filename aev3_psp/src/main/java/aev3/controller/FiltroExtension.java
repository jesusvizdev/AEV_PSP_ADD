package aev3.controller;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Clase que implementa la interfaz FilenameFilter para filtrar archivos por su
 * extensión.
 */
public class FiltroExtension implements FilenameFilter {

	String extension;

	/**
	 * Constructor de la clase que recibe la extensión a filtrar.
	 *
	 * @param extension La extensión de archivo a filtrar (por ejemplo, ".txt").
	 */
	public FiltroExtension(String extension) {
		this.extension = extension;
	}

	/**
	 * Método de la interfaz FilenameFilter que determina si un archivo debe ser
	 * aceptado o no.
	 *
	 * @param dir  El directorio que contiene el archivo.
	 * @param name El nombre del archivo.
	 * @return true si el archivo cumple con la extensión, false de lo contrario.
	 */
	public boolean accept(File dir, String name) {
		return name.endsWith(extension);
	}

}
