package es.florida.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Servidor.
 */
public class Servidor {

	static List<Thread> listaConexiones = new ArrayList<Thread>();
	static List<Peticion> listaPeticiones = new ArrayList<Peticion>();

	/**
	 * Metodo ejecutable que crea un servidor en bucle para escuchar a tantos clientes como quiera. Para cada cliente se crea un hilo.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		int numPuerto = 7777;
		ServerSocket servidor = null;
		try {
			servidor = new ServerSocket(numPuerto);
		} catch (IOException e) {
			System.out.println("SERVIDOR >>> Error");
		}
		System.out.println("SERVIDOR >>> Servidor arrancado. Esperando peticion ...");

		while (true) {
			Socket cliente = servidor.accept();
			System.out.println("SERVIDOR >>> Conexion recibida. Lanzando nuevo hilo ...");
			Peticion p = new Peticion(cliente, listaConexiones, listaPeticiones);
			Thread hiloCliente = new Thread(p);
			listaConexiones.add(hiloCliente);
			hiloCliente.start();
		}
	}

}
