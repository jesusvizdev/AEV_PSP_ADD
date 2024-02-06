package es.florida.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Clase Cliente que recibe datos del Servidor.
 */
public class ClienteReceptor implements Runnable {

	private Socket cliente;
	BufferedReader br;
	private String mensaje = "";

	/**
	 * Constructor Cliente Receptor.
	 * @param cliente Recibe como parametro la conexion del cliente.
	 */
	public ClienteReceptor(Socket cliente) {
		this.cliente = cliente;
	}

	/**
	 * Metodo ejecutable para el hilo receptor. Recibe mensajes del Servidor. El mensaje "ok" hace referencia a la conexion correcta de los clientes.
	 */
	@Override
	public void run() {
		while (!mensaje.equals("ok")) {
			try {
				InputStreamReader isr = new InputStreamReader(cliente.getInputStream());
				br = new BufferedReader(isr);
				String resultado = br.readLine();
				mensaje = resultado;

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		receiveOptions();
		System.out.println("Sesion finalizada correctamente.");
		System.exit(0);
	}

	/**
	 * Metodo getter del mensaje para que el Cliente Transmisor pueda obtenerlo.
	 * @return Retorna el mensaje.
	 */
	public String getMensaje() {
		return mensaje;
	}

	/**
	 * Metodo donde el hilo del Cliente Receptor se ejecuta en bucle recibiendo informacion hasta que el mensaje sea exit.
	 */
	public void receiveOptions() {
		while (true) {
			InputStreamReader isr;
			try {
				isr = new InputStreamReader(cliente.getInputStream());
				br = new BufferedReader(isr);
				String resultado = br.readLine();
				if (resultado.equals("exit")) {
					mensaje = resultado;
					br.close();
					isr.close();
					cliente.close();
					return;
				}
				System.out.println(resultado);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
