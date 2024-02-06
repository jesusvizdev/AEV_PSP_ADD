package aev3.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para manejar solicitudes relacionadas con películas y usuarios.
 */
@RestController
public class Controller {

	private String directorioData = "pelisVacia";
	private String archivoAutorizados = "autorizados.txt";

	/**
	 * Método para mostrar información sobre películas.
	 *
	 * @param strId Se puede informacion de todas las peliculas si el parámetro es
	 *              igual a "all" o de una sola pelicula si es igual a su id.
	 * @return Una respuesta HTTP con información de las películas en formato JSON.
	 */
	@GetMapping("/APIpelis/t")
	ResponseEntity<String> showJSON(@RequestParam(value = "id") String strId) {

		String strJSON = "";

		if (strId.equals("all")) {
			System.out.println("Peticion GET. Solicitud de todas las peliculas.");
			File directorio = new File(directorioData);
			String[] listaFicheros = directorio.list(new FiltroExtension(".txt"));
			strJSON = "{“titulos”: [";
			if (listaFicheros != null) {
				for (String fichero : listaFicheros) {
					try {
						FileReader fr = new FileReader(directorio + File.separator + fichero);
						BufferedReader br = new BufferedReader(fr);
						String id = fichero.split(".txt")[0];
						String titulo = br.readLine().split(": ")[1];
						strJSON = strJSON + "{\"id\": \"" + id + "\", \"titulo\": \"" + titulo + "\"},";
						System.out.println("Id: " + id);
						System.out.println("Titulo: " + titulo);
						br.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (strJSON.endsWith(",")) {
					strJSON = strJSON.substring(0, strJSON.length() - 1);
				}
			} else if (listaFicheros == null)
				System.out.println("El directorio no contiene datos de peliculas.");

			strJSON = strJSON + "]}";
			System.out.println("JSON en formato String: " + strJSON);

		} else {
			System.out.println("Peticion GET. Solicitud de una pelicula por su id.");
			strJSON = "{";
			File directorio = new File(directorioData);
			String[] listaFicheros = directorio.list(new FiltroExtension(".txt"));
			boolean idExist = false;

			for (String fichero : listaFicheros) {
				String id = fichero.split(".txt")[0];
				if (id.equals(strId)) {
					try {
						idExist = true;
						FileReader fr = new FileReader(directorio + File.separator + fichero);
						BufferedReader br = new BufferedReader(fr);
						System.out.println("Id: " + id);
						String titulo = br.readLine().split(": ")[1];
						System.out.println("Titulo: " + titulo);
						System.out.println("Resenias: ");
						strJSON = strJSON + "\"id\": \"" + id + "\", \"titulo\": \"" + titulo + "\", \"resenyas\": [";
						String resenia = br.readLine();
						if (resenia == null) {
							System.out.println("No hay ninguna resenia.");
							strJSON = strJSON + "]}";
						}
						while (resenia != null) {
							System.out.println(resenia);
							strJSON = strJSON + "\"" + resenia + "\",";
							resenia = br.readLine();
						}
						if (strJSON.endsWith(",")) {
							strJSON = strJSON.substring(0, strJSON.length() - 1);
							strJSON = strJSON + "]}";
						}
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			if (!idExist) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ERROR 404: Film Id not found...");
			}
		}

		return ResponseEntity.status(HttpStatus.OK).body(strJSON);
	}

	/**
	 * Método para agregar una nueva reseña.
	 *
	 * @param cuerpoPeticion Cuerpo de la solicitud en formato JSON con información
	 *                       de la reseña.
	 * @return Una respuesta HTTP indicando el resultado de la operación.
	 */
	@PostMapping("/APIpelis/nuevaResenya")
	ResponseEntity<Object> postReview(@RequestBody String cuerpoPeticion) {
		System.out.println("Peticion POST. Aniadir una nueva resenya.");
		JSONObject obj = new JSONObject(cuerpoPeticion);
		String usuario = (String) obj.get("usuario");
		boolean autorizado = isRegistered(usuario);
		if (!autorizado) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String id = (String) obj.getString("id");
		String resenya = (String) obj.get("resenya");
		System.out.println("Datos --> " + "Id: " + id + " Usuario: " + usuario + " Resenya: " + resenya);
		aniadirResenya(usuario, id, resenya);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

	}

	/**
	 * Método para agregar una nueva película.
	 *
	 * @param cuerpoPeticion Cuerpo de la solicitud en formato JSON con información
	 *                       de la película.
	 * @return Una respuesta HTTP indicando el resultado de la operación.
	 */
	@PostMapping("/APIpelis/nuevaPeli")
	ResponseEntity<Object> postFilm(@RequestBody String cuerpoPeticion) {
		System.out.println("Peticion POST. Aniadir una nueva pelicula.");
		JSONObject obj = new JSONObject(cuerpoPeticion);
		String usuario = (String) obj.get("usuario");
		boolean autorizado = isRegistered(usuario);
		if (!autorizado) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String titulo = (String) obj.getString("titulo");
		System.out.println("Datos --> " + "Usuario: " + usuario + " Titulo: " + titulo);
		aniadirPelicula(titulo);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

	}

	/**
	 * Método para agregar un nuevo usuario.
	 *
	 * @param cuerpoPeticion Cuerpo de la solicitud en formato JSON con información
	 *                       del nuevo usuario.
	 * @return Una respuesta HTTP indicando el resultado de la operación.
	 */
	@PostMapping("/APIpelis/nuevoUsuario")
	ResponseEntity<Object> newUser(@RequestBody String cuerpoPeticion) {
		System.out.println("Peticion POST. Aniadir una nuevo usuario.");
		JSONObject obj = new JSONObject(cuerpoPeticion);
		String usuario = (String) obj.get("usuario");
		boolean operacionCorrecta = aniadirUsuario(usuario);
		if (operacionCorrecta)
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body("El usuario ya existe. No se pudo realizar la operación.");
	}

	/**
	 * Método para añadir una nueva reseña a un archivo.
	 *
	 * @param usuario El nombre del usuario que añade la reseña.
	 * @param id      El ID de la película a la que se añade la reseña.
	 * @param resenya La nueva reseña a añadir.
	 */
	private void aniadirResenya(String usuario, String id, String resenya) {
		try {
			FileWriter fw = new FileWriter(directorioData + File.separator + id + ".txt", true);
			fw.write("\n" + usuario + ": " + resenya);
			System.out.println("Resenya publicada en " + id + ".txt");
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método para añadir una nueva película.
	 *
	 * @param titulo El título de la nueva película.
	 */
	private void aniadirPelicula(String titulo) {
		File directorio = new File(directorioData);
		String[] listaFicheros = directorio.list(new FiltroExtension(".txt"));
		String id = "";
		if (listaFicheros == null) {
			id = "1";
		} else {
			id = String.valueOf(listaFicheros.length + 1);
			try {
				File nuevoFichero = new File(directorio + File.separator + id + ".txt");
				FileWriter fw = new FileWriter(nuevoFichero);
				fw.write("Titulo: " + titulo);
				System.out.println("Nueva pelicula como fichero " + id + ".txt");
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Método para añadir un nuevo usuario a la lista de autorizados.
	 *
	 * @param usuario El nombre del nuevo usuario.
	 * @return true si la operación fue exitosa, false si el usuario ya existe.
	 */
	private boolean aniadirUsuario(String usuario) {
	    try (FileWriter fw = new FileWriter(archivoAutorizados, true);
	         FileReader fr = new FileReader(archivoAutorizados);
	         BufferedReader br = new BufferedReader(fr)) {

	        String linea = br.readLine();
	        if (linea == null) {
	            fw.write(usuario);
	            System.out.println("Nuevo usuario registrado como " + usuario);
	            return true;
	        } else {
	            boolean usuarioExiste = isRegistered(usuario);
	            if (usuarioExiste) {
	                return false;
	            }
	        }

	        fw.write("\n" + usuario);
	        System.out.println("Nuevo usuario registrado como " + usuario);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return true;
	}

	/**
	 * Método para verificar si un usuario está registrado.
	 *
	 * @param usuario El nombre del usuario a verificar.
	 * @return true si el usuario está registrado, false si no.
	 */
	private boolean isRegistered(String usuario) {
		try {
			FileReader fr = new FileReader(archivoAutorizados);
			BufferedReader br = new BufferedReader(fr);
			String linea;
			while ((linea = br.readLine()) != null) {
				if (linea.equals(usuario)) {
					br.close();
					return true;
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
