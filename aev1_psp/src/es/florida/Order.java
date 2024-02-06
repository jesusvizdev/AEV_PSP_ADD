package es.florida;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.awt.event.ActionEvent;

/**
 * Es la clase que muestra la interfaz gráfica y contiene el lanzador para el multiproceso.
 */
public class Order {

	private JFrame frame;
	private JTextField txtTypeI;
	private JTextField txtTypeO;
	private JTextField txtTypeT;
	private JTextField txtTypeJ;
	private JTextField txtTypeL;
	private JTextField txtTypeS;
	private JTextField txtTypeZ;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JTextField txtNombreFichero;
	private String[] cantidadPiezas = new String[7];
	private static String salida;
	private static String ficheroSalida;
	
	
	/**
	 * Lanzador del multiproceso hacia la clase Manufacture. Todos sus parámetros son la cantidad de cada tipo de pieza que serán necesarios para el posterior proceso multihilo.
	 * @param cantidadI Pieza de tipo I
	 * @param cantidadO Pieza de tipo O
	 * @param cantidadT Pieza de tipo T
	 * @param cantidadJ Pieza de tipo J
	 * @param cantidadL Pieza de tipo L
	 * @param cantidadS Pieza de tipo S
	 * @param cantidadZ Pieza de tipo Z
	 */
	public void lanzarManufacture(String cantidadI, String cantidadO, String cantidadT, String cantidadJ, String cantidadL, String cantidadS, String cantidadZ) {
		
		try {

		String className = "es.florida.Manufacture";
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		String classpath = System.getProperty("java.class.path");
		
		List<String> command = new ArrayList<String>();
		command.add(javaBin);
		command.add("-cp");
		command.add(classpath);
		command.add(className);
		command.add(String.valueOf(cantidadI));
		command.add(String.valueOf(cantidadO));
		command.add(String.valueOf(cantidadT));
		command.add(String.valueOf(cantidadJ));
		command.add(String.valueOf(cantidadL));
		command.add(String.valueOf(cantidadS));
		command.add(String.valueOf(cantidadZ));
		
		ProcessBuilder pb = new ProcessBuilder(command);
		
		Process process;
		if (salida.equals("consola")) {
			 process = pb.inheritIO().start();
		}else if (salida.equals("fichero")) {
			 process = pb.redirectOutput(new File(ficheroSalida)).start();
		     JOptionPane.showMessageDialog(null, "El fichero " + ficheroSalida + " ha sido creado correctamente. Es posible que la informacion aun no este disponible.");

		}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Método ejecutable que lanza la aplicación y hace visible la interfaz.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Order window = new Order();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	
	/**
	 * Método que obtiene la cantidad de cada tipo de pieza a fabricar.
	 */
	public void getInformacionTetrominos() {
		
		cantidadPiezas[0] = txtTypeI.getText();
		cantidadPiezas[1] = txtTypeO.getText();
		cantidadPiezas[2] = txtTypeT.getText();
		cantidadPiezas[3] = txtTypeJ.getText();
		cantidadPiezas[4] = txtTypeL.getText();
		cantidadPiezas[5] = txtTypeS.getText();
		cantidadPiezas[6] = txtTypeZ.getText();
	}
	
	/**
	 * Método que maneja posibles errores desde la interfaz. Si un campo se deja en blanco o se escribe un valor negativo sera como escribir 0 piezas.
	 */
	public void manageErrors() {
		
		for (int i = 0; i<cantidadPiezas.length; i++) {
			
			if (cantidadPiezas[i].equals("")) {
				cantidadPiezas[i] = "0";
			}
			
			int cantidadInteger = Integer.parseInt(cantidadPiezas[i]);
			
			if (cantidadInteger <= -1) {
				cantidadPiezas[i] = "0";
			}
		}
	}
	
	/**
	 * Este método crea un fichero si la salida se elige desde fichero. También se asegura que tenga una extensión adecuada y lanza el proceso.
	 * @param nombreFichero
	 */
	public void crearFichero(String nombreFichero) {
		
		if (!nombreFichero.endsWith(".txt")){
	        JOptionPane.showMessageDialog(null, "El nombre del fichero debe finalizar en la extension .txt", "Error", JOptionPane.ERROR_MESSAGE);
	        return;
		}
		
	try {
			File fichero = new File(nombreFichero);
			fichero.createNewFile();
			ficheroSalida = nombreFichero;
			lanzarManufacture(cantidadPiezas[0], cantidadPiezas[1], cantidadPiezas[2], cantidadPiezas[3], cantidadPiezas[4], cantidadPiezas[5], cantidadPiezas[6]);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	
	/**
	 * Método que crea e inicializa la aplicación.
	 */
	public Order() {
		initialize();
	}

	
	/**
	 * Inicializa los componentes de la interfaz gráfica. Contiene un Action Listener para un botón que se encarga de obtener la información de la interfaz y las posibles salidas de la clase Manufacture.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 517, 358);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("TIPO");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblNewLabel.setBounds(70, 77, 47, 13);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("FABRICACION DE TETROMINOS");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblNewLabel_1.setBounds(48, 26, 291, 13);
		frame.getContentPane().add(lblNewLabel_1);
		
		JLabel lblCantidad = new JLabel("CANTIDAD");
		lblCantidad.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblCantidad.setBounds(134, 77, 71, 13);
		frame.getContentPane().add(lblCantidad);
		
		JLabel lblI = new JLabel("I");
		lblI.setForeground(Color.CYAN);
		lblI.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblI.setBounds(80, 114, 15, 13);
		frame.getContentPane().add(lblI);
		
		JLabel lblO = new JLabel("O");
		lblO.setForeground(Color.YELLOW);
		lblO.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblO.setBounds(80, 137, 15, 13);
		frame.getContentPane().add(lblO);
		
		JLabel lblT = new JLabel("T");
		lblT.setForeground(Color.MAGENTA);
		lblT.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblT.setBounds(80, 160, 15, 13);
		frame.getContentPane().add(lblT);
		
		JLabel lblJ = new JLabel("J");
		lblJ.setForeground(Color.BLUE);
		lblJ.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblJ.setBounds(80, 183, 15, 13);
		frame.getContentPane().add(lblJ);
		
		JLabel lblL = new JLabel("L");
		lblL.setForeground(Color.ORANGE);
		lblL.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblL.setBounds(80, 206, 15, 13);
		frame.getContentPane().add(lblL);
		
		JLabel lblS = new JLabel("S");
		lblS.setForeground(Color.GREEN);
		lblS.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblS.setBounds(80, 229, 15, 13);
		frame.getContentPane().add(lblS);
		
		JLabel lblZ = new JLabel("Z");
		lblZ.setForeground(Color.RED);
		lblZ.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblZ.setBounds(80, 252, 15, 13);
		frame.getContentPane().add(lblZ);
		
		txtTypeI = new JTextField();
		txtTypeI.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtTypeI.setText("0");
		txtTypeI.setBounds(141, 112, 47, 19);
		frame.getContentPane().add(txtTypeI);
		txtTypeI.setColumns(10);
		
		txtTypeO = new JTextField();
		txtTypeO.setText("0");
		txtTypeO.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtTypeO.setColumns(10);
		txtTypeO.setBounds(141, 135, 47, 19);
		frame.getContentPane().add(txtTypeO);
		
		txtTypeT = new JTextField();
		txtTypeT.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtTypeT.setText("0");
		txtTypeT.setColumns(10);
		txtTypeT.setBounds(141, 158, 47, 19);
		frame.getContentPane().add(txtTypeT);
		
		txtTypeJ = new JTextField();
		txtTypeJ.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtTypeJ.setText("0");
		txtTypeJ.setColumns(10);
		txtTypeJ.setBounds(141, 181, 47, 19);
		frame.getContentPane().add(txtTypeJ);
		
		txtTypeL = new JTextField();
		txtTypeL.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtTypeL.setText("0");
		txtTypeL.setColumns(10);
		txtTypeL.setBounds(141, 204, 47, 19);
		frame.getContentPane().add(txtTypeL);
		
		txtTypeS = new JTextField();
		txtTypeS.setText("0");
		txtTypeS.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtTypeS.setColumns(10);
		txtTypeS.setBounds(141, 227, 47, 19);
		frame.getContentPane().add(txtTypeS);
		
		txtTypeZ = new JTextField();
		txtTypeZ.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtTypeZ.setText("0");
		txtTypeZ.setColumns(10);
		txtTypeZ.setBounds(141, 250, 47, 19);
		frame.getContentPane().add(txtTypeZ);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		panel.setBounds(51, 64, 180, 226);
		frame.getContentPane().add(panel);
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("Fichero de texto");
		buttonGroup.add(rdbtnNewRadioButton);
		rdbtnNewRadioButton.setBounds(277, 111, 136, 21);
		frame.getContentPane().add(rdbtnNewRadioButton);
		
		JLabel lblSalidasManufacture = new JLabel("SALIDAS MANUFACTURE");
		lblSalidasManufacture.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblSalidasManufacture.setBounds(277, 78, 212, 13);
		frame.getContentPane().add(lblSalidasManufacture);
		
		JRadioButton rdbtnConsola = new JRadioButton("Consola");
		rdbtnConsola.setSelected(true);
		buttonGroup.add(rdbtnConsola);
		rdbtnConsola.setBounds(277, 198, 136, 21);
		frame.getContentPane().add(rdbtnConsola);
		
		
		JButton btnEjecutar = new JButton("EJECUTAR");
		btnEjecutar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				getInformacionTetrominos();
				manageErrors();
				
				Enumeration<?> radioButtons = buttonGroup.getElements();
				int numButtons = buttonGroup.getButtonCount();
				for (int i = 0; i < numButtons; i++) {
				AbstractButton radioButton = (AbstractButton) radioButtons.nextElement();
					if (radioButton.isSelected()) {
						String buttonName = radioButton.getText();
						if (buttonName.equals("Consola")) {
							salida = "consola";
							lanzarManufacture(cantidadPiezas[0], cantidadPiezas[1], cantidadPiezas[2], cantidadPiezas[3], cantidadPiezas[4], cantidadPiezas[5], cantidadPiezas[6]);
						}else if(buttonName.equals("Fichero de texto")) {
							salida = "fichero";
							String nombreFichero = txtNombreFichero.getText();
							crearFichero(nombreFichero);
						}
							
					}
				}
				
			}
		});
		btnEjecutar.setBounds(353, 269, 108, 21);
		frame.getContentPane().add(btnEjecutar);
		
		txtNombreFichero = new JTextField();
		txtNombreFichero.setBounds(287, 158, 126, 19);
		frame.getContentPane().add(txtNombreFichero);
		txtNombreFichero.setColumns(10);
		
		JLabel lblIntroduceElNombre = new JLabel("Introduce el nombre del fichero (.txt)");
		lblIntroduceElNombre.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblIntroduceElNombre.setBounds(287, 138, 212, 13);
		frame.getContentPane().add(lblIntroduceElNombre);
	}
	
	
	
}
