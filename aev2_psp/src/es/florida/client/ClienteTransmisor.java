package es.florida.client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Clase del cliente Transmisor y que ejecuta la conexion del Cliente con el Servidor.
 */
public class ClienteTransmisor implements Runnable {

	static Scanner scanner = new Scanner(System.in);
	static Socket cliente = null;
	static PrintWriter pw;
	static String user;
	static OutputStreamWriter osw;

	/**
	 * Constructor del Cliente Transmisor. Ejecuta la conexion con el servidor y lanza los hilos de transmision y recepcion de mensajes.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		boolean connection = false;

		while (!connection) {
			System.out.print("IP: ");
			String ip = scanner.nextLine();
			System.out.print("Puerto: ");
			try {
				int puerto = scanner.nextInt();
				cliente = new Socket(ip, puerto);
				connection = true;
			} catch (IOException | InputMismatchException e) {
				System.out.println("Error en la conexion con el servidor.");
				scanner.nextLine();
			}
		}

		ClienteTransmisor ct = new ClienteTransmisor();
		Thread hiloTransmisor = new Thread(ct);
		hiloTransmisor.start();
	}

	/**
	 * Metodo ejecutable del hilo transmisor. 
	 */
	@Override
	public void run() {

		ClienteReceptor cr = new ClienteReceptor(cliente);
		Thread hiloReceptor = new Thread(cr);
		hiloReceptor.start();
		while (!cr.getMensaje().equals("ok")) {
			sendUserData();
			try {
				System.out.println("Comprobando credenciales ...");
				Thread.sleep(1500);
				if (cr.getMensaje().equals("no")) {
					System.out.println("Credenciales incorrectas.");
				}
				if (cr.getMensaje().equals("error")) {
					System.out.println("Error: ya existe una sesion iniciada con ese usuario.");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		System.out.println("Credenciales correctas.");
		System.out.println("Introduce la operacion que deseas realizar: ");
		System.out.println("---------------");
		sendUserOptions(cr);

		try {
			osw.close();
			pw.close();
			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Metodo que envia datos a los demas usuarios a traves del servidor. Ejecuta lo relacionado con la identificacion de los usuarios.
	 */
	public void sendUserData() {

		System.out.println("AUTENTICACION: ");

		boolean control = false;
		while (!control) {
			System.out.print("User: ");
			user = scanner.next();
			String espacios = "";
			espacios = scanner.nextLine();
			if (espacios.length() < 1) {
				control = true;
			} else {
				System.out.println("El usuario no puede tener espacios.");
			}
		}

		System.out.print("Password: ");
		String password = scanner.nextLine();

		try {
			osw = new OutputStreamWriter(cliente.getOutputStream());
			pw = new PrintWriter(osw);
			pw.println(user + ";" + password);
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Metodo en bucle para transmitir mensajes hasta que la operacion sea exit. Envia las operaciones existentes al servidor para la comunicacion con otros clientes.
	 * @param cr
	 */
	public void sendUserOptions(ClienteReceptor cr) {
		while (!cr.getMensaje().equals("exit")) {
			String operacion = scanner.nextLine();
			try {
				osw = new OutputStreamWriter(cliente.getOutputStream());
				pw = new PrintWriter(osw);
				pw.println(operacion);
				pw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
