package es.florida;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Semaphore;

/**
 * La clase pieza defiene cada pieza por su tipo y su tiempo de fabricación. Contiene el método run al que se dirigen los hilos y un semáforo que funciona de controlador para los mismos.
 */
public class Pieza implements Runnable {
	
	private String tipo;
	private int tiempoFabricacion;
	private Semaphore maquinas;
	
	/**
	 * Constructor de la clase Pieza
	 * @param tipo Recibe el tipo de pieza
	 * @param tiempoFabricacion Recibe el tiempo de fabricación en milisegundos
	 * @param maquinas Recibe una instancia Semáforo para 8 hilos.
	 */
	public Pieza(String tipo,int tiempoFabricacion,Semaphore maquinas) {
		this.tipo = tipo;
		this.tiempoFabricacion = tiempoFabricacion;
		this.maquinas = maquinas;
	}

	/**
	 *Es el método sobreescrito de implementar Runnable. Ejecuta el proceso de cada pieza, obtiene información de cada pieza y la añade a una lista global.
	 *El método hace uso de la instacia Semaforo para simular una cola de 8 hilos, pues existen 8 máquinas.
	 */
	@Override
	public void run() {
			try {
					System.out.println(Thread.currentThread().getName() + " intentando adquirir una maquina...");
					maquinas.acquire();
		            System.out.println(Thread.currentThread().getName() + " ha adquirido una maquina y comenzara el proceso de fabricacion.");
					procesoFabricacion(tiempoFabricacion);
					String infoPieza = tipo + "_" + getInfoPieza();
					Manufacture.aniadirInfoPieza(infoPieza);
					System.out.println(infoPieza);
					
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
	            System.out.println(Thread.currentThread().getName() + " ha liberado la maquina.");
				maquinas.release();
			}
	}
	
	/**
	 * Método que se encarga de la fabricación de una pieza. Funciona como un simulador que simula ocupaciones de máquina con interaciones.
	 * @param tiempoFabricacion Recibe como parámetro un entero que son los milisegundos que tarda cada pieza en fabricarse.
	 */
	public static void procesoFabricacion(int tiempoFabricacion) {
		 long tiempoInicio = System.currentTimeMillis();
		 long tiempoFin = tiempoInicio + tiempoFabricacion; 
		 int iteraciones = 0;
		 while (System.currentTimeMillis() < tiempoFin) {
		 iteraciones++;
		 }
		}
	
	/**
	 * Es un método que da información sobre cada pieza. Esto es, una cadena formateada en AñoMesDia_HoraMinutoSegundo de un momento exacto.
	 * @return Retorna el string con formato actualizado.
	 */
	public String getInfoPieza() {
		Date fechaHoraActual = new Date();
		SimpleDateFormat formato = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String formatoActualizado = formato.format(fechaHoraActual);
		return formatoActualizado;	
	}
}
