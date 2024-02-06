package es.florida.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Clase ejecutable para cada hilo.
 */
public class Peticion implements Runnable {

	private List<Thread> listaConexiones;
	private Socket conexion;
	private BufferedReader br;
	private PrintWriter pw;
	private String verify = "";
	private String userName;
	private String password;
	private List<Peticion> listaPeticiones;

	/**
	 * @param conexion Recibe la conexion.
	 * @param listaConexiones Recibe la lista de conexiones de los clientes.
	 * @param listaPeticiones Recibe la lista de Peticiones de cada cliente.
	 */
	public Peticion(Socket conexion, List<Thread> listaConexiones, List<Peticion> listaPeticiones) {
		this.conexion = conexion;
		this.listaConexiones = listaConexiones;
		this.listaPeticiones = listaPeticiones;
	}

	/**
	 * Metodo ejecutable del hilo que obtiene los datos de los usuarios y los verifica. Tambien inicia la comunicacion. 
	 */
	@Override
	public void run() {
		while (!verify.equals("ok")) {
			System.out.println("AUTENTICACION");
			getUserData();
			verify = verifyUser();
			try {
				System.out.println("Enviando respuesta al cliente: " + verify);
				OutputStreamWriter osw = new OutputStreamWriter(conexion.getOutputStream());
				PrintWriter pw = new PrintWriter(osw);
				pw.println(verify);
				pw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Thread.currentThread().setName(userName);
		startComunication();

	}

	/**
	 * Metodo para obtener los datos de los usuarios.
	 */
	public void getUserData() {
		String[] infoUser = null;
		;
		try {
			InputStreamReader isr = new InputStreamReader(conexion.getInputStream());
			br = new BufferedReader(isr);
			infoUser = br.readLine().split(";");
			setUserName(infoUser[0]);
			setPassword(infoUser[1]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Metodo que verifica el usuario a traves de un fichero donde se encuentran los nombres y contrasenias.
	 * @return Retorna "ok" si se verifica correctamente o "error" y "no" para posibles errores.
	 */
	public String verifyUser() {
		File archivo = new File("users.txt");
		try {
			FileReader fr = new FileReader(archivo);
			br = new BufferedReader(fr);
			String linea;
			while ((linea = br.readLine()) != null) {
				String[] datos = linea.split(";");
				String user = datos[0];
				String pass = datos[1];

				if (getUserName().equals(user) && getPassword().equals(pass)) {
					for (Thread conexion : listaConexiones) {
						if (conexion.getName().equals(userName)) {
							return "error";
						}
					}
					listaPeticiones.add(this);
					return "ok";
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return "no";
	}

	/**
	 * Metodo que inicia la comunicacion y gestiona las posibles operaciones que un cliente puede hacer.
	 */
	public void startComunication() {
		boolean comunicacion = true;
		String nombreHilo = Thread.currentThread().getName();

		while (comunicacion) {
			try {
				InputStreamReader isr = new InputStreamReader(conexion.getInputStream());
				br = new BufferedReader(isr);
				String operacion = br.readLine();
				String timestamp = getTimestamp();
				System.out.println(timestamp + ": " + nombreHilo + " >>> " + operacion);

				if (operacion.equals("?")) {
					timestamp = getTimestamp();
					String listaUsuarios = timestamp + ": Usuarios conectados: ";
					for (Thread conexion : listaConexiones) {
						listaUsuarios += conexion.getName() + " | ";
					}
					OutputStreamWriter osw = new OutputStreamWriter(conexion.getOutputStream());
					pw = new PrintWriter(osw);
					pw.println(listaUsuarios);
					pw.flush();
				}

				else if (operacion.equals("exit")) {
					OutputStreamWriter osw = new OutputStreamWriter(conexion.getOutputStream());
					pw = new PrintWriter(osw);
					pw.println(operacion);
					pw.flush();
					listaConexiones.remove((Thread.currentThread()));
					osw.close();
					isr.close();
					comunicacion = false;
				} else if (operacion.startsWith("@")) {
					String usuarioDestino = operacion.substring(1).split(" ")[0];
					sendMessage(operacion, usuarioDestino);
				} else {
					timestamp = getTimestamp();
					broadcast(timestamp + ": " + Thread.currentThread().getName() + " >>> " + operacion);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String timestamp = getTimestamp();
		System.out.println(timestamp + ": Conexion finalizada con " + nombreHilo);
		try {
			br.close();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Metodo que crea un timestamp en un formato determinado.
	 * @return Retorna el timestamp en formato String.
	 * 
	 */
	public String getTimestamp() {
		Date now = new Date();
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyy-HH:mm:ss");
		String timestamp = formato.format(now);
		return timestamp;
	}

	/**
	 * Metodo que envia un mensaje a todos los clientes conectados excepto al cliente que lo envia.
	 * @param message Recibe como parametro el mensaje a enviar.
	 */
	public void broadcast(String message) {
		for (Peticion peticion : listaPeticiones) {
			if (!peticion.getUserName().equals(userName)) {
				try {
					OutputStreamWriter osw = new OutputStreamWriter(peticion.conexion.getOutputStream());
					PrintWriter pw = new PrintWriter(osw);
					pw.println(message);
					pw.flush();
				} catch (IOException e) {
				}
			}
			if (peticion.getUserName().equals(userName)) {
				try {
					OutputStreamWriter osw = new OutputStreamWriter(peticion.conexion.getOutputStream());
					PrintWriter pw = new PrintWriter(osw);
					String timestamp = getTimestamp();
					pw.println(timestamp + " : Mensaje enviado a todos los usuarios conectados");
					pw.flush();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Metodo para enviar un mensaje a un usuario determinado.
	 * @param operacion Recibe la operacion que se dea realizar.
	 * @param usuarioDestino Recibe el nombre del usuario destino.
	 */
	public void sendMessage(String operacion, String usuarioDestino) {
		for (Peticion peticion : listaPeticiones) {
			if (peticion.getUserName().equals(usuarioDestino)) {
				try {
					OutputStreamWriter osw = new OutputStreamWriter(peticion.conexion.getOutputStream());
					PrintWriter pw = new PrintWriter(osw);
					String timeStamp = getTimestamp();
					pw.println(timeStamp + ": " + Thread.currentThread().getName() + " (privado) >>> " + operacion);
					pw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (peticion.getUserName().equals(this.getUserName())) {
				try {
					OutputStreamWriter osw = new OutputStreamWriter(peticion.conexion.getOutputStream());
					PrintWriter pw = new PrintWriter(osw);
					String timeStamp = getTimestamp();
					pw.println(timeStamp + ": Mensaje privado enviado a " + usuarioDestino);
					pw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String getUserName() {
		return userName;
	}

	/**
	 * Metodo setter del nombre de usuario.
	 * @param userName Recibe como parametro el usuario.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Metodo getter de la contrasenia.
	 * @return Retorna la contrasenia.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Metodo setter para la contrasenia
	 * @param password Recibe como parametro la contrasenia.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

}
