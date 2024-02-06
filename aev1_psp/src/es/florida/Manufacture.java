package es.florida;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Clase que recibe los parametros de la interfaz ejecuta el proceso multihilo.
 */
public class Manufacture {
	
	private static int totalPiezas = 0;
	private static Semaphore maquinas = new Semaphore(8);
	private static List<String> listaPiezasGlobal = new ArrayList<String>();

	
	/**
	 * Método ejecutable que crea los hilos necesarios para ejecutar el método run().
	 * Recibe los parámetros necesarios a partir de args[]. 
	 * Indica mediante sysos información como cantidad total de piezas y los detalles de la fabricación.
	 * @param args Son los parámetros del Lanzador multiproceso.
	 */
	public static void main(String[] args) {
		
		String[] tiposPiezas = {"I","O","T","J","L","S","Z"};
		int[] tiempoFabricacion = {1000,2000,3000,4000,4000,5000,5000};
		int diferentesPiezas = tiposPiezas.length;
		int[] cantidadPiezas = new int[diferentesPiezas];
		
		for (int i=0; i<args.length; i++) {
			cantidadPiezas[i] = Integer.parseInt(args[i]);
			totalPiezas += cantidadPiezas[i];
		}
		
		System.out.println("El total de piezas a fabricar es:" + " " + totalPiezas);
		
		Pieza[] piezas = new Pieza[totalPiezas];
		Thread[] hiloPiezas = new Thread[totalPiezas];
		
		System.out.println("-------------------------");

		System.out.println("Detalles de fabricacion: ");
		System.out.println();
		
		int index = 0;
	    for (int i = 0; i < diferentesPiezas; i++) {
	        for (int j = 0; j < cantidadPiezas[i]; j++) {
	            piezas[index] = new Pieza(tiposPiezas[i], tiempoFabricacion[i], maquinas);
	            hiloPiezas[index] = new Thread(piezas[index]);
	            hiloPiezas[index].setName(tiposPiezas[i]);
	            hiloPiezas[index].start();
	            index++;
	        }
	    }
	    
		for (Thread pieza : hiloPiezas) {
			try {
				pieza.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("-------------------------");
		System.out.println("El proceso de fabricacion ha finalizado.");
		System.out.println("Total de piezas fabricadas: " + listaPiezasGlobal.size());
		
		System.out.println("-------------------------");
		String formatoActualizado = setTimeStamp();
		String ficheroCreado = writeFichero(formatoActualizado);
		
		System.out.println("Fichero timestamp creado correctamente: " + ficheroCreado);
		
	}
	
	/**
	 * Añade la información de cada pieza hacia una lista global
	 * @param infoPieza
	 */
	public static synchronized void aniadirInfoPieza(String infoPieza) {
		listaPiezasGlobal.add(infoPieza);
	}
	
	/**
	 * Crea un string con la información de fecha y hora formateada en un momento exacto.
	 * @return Retorna el string correctamente formateado
	 */
	public static String setTimeStamp() {
		Date horaFechaActual = new Date();
		SimpleDateFormat formato = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String formatoActualizado = formato.format(horaFechaActual);
		return formatoActualizado;
	}
	
	/**
	 * Método que crea un fichero, lee la lista global de información y escribe esa información en el archivo.
	 * @param nombre Recibe el formato actualizado. Es utilizado para crear el nombre de un fichero timestamp.
	 * @return Retorna el nombre del fichero
	 */
	public static String writeFichero(String nombre) {
		String nombreFichero = "LOG_" + nombre + ".txt";
		File fichero = new File(nombreFichero);
		try {
			FileWriter fw = new FileWriter(fichero);
			BufferedWriter bw = new BufferedWriter(fw);
			for (String linea : listaPiezasGlobal) {
				bw.write(linea);
				bw.newLine();
			}
			bw.close();
			fw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return nombreFichero;
	}
	
}
