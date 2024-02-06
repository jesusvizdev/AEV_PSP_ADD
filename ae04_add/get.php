<?php

/**
 * Script PHP para obtener datos de clima de la base de datos para una ciudad específica.
 *
 * Este script espera recibir el parámetro 'nombre' a través de una solicitud GET.
 * Utiliza este parámetro para consultar la base de datos y devuelve los resultados en formato JSON.
 */
if (isset($_GET["nombre"])) {
    $nombre = $_GET["nombre"];
    $servidor = "localhost";
    $usuario = "root";
    $password = "";
    $dbname = "clima";
    $conexion = mysqli_connect($servidor, $usuario, $password, $dbname);

    if (!$conexion) {
        echo "Error en la conexión a MySQL: " . mysqli_connect_error();
        exit();
    }

    $sql = "SELECT * FROM ciudad WHERE nombre = '" . $nombre . "'";

    $resultado = mysqli_query($conexion, $sql);

    if (!$resultado) {
        die("Error en la consulta: " . mysqli_error($conexion));
    }

    $datos = array();

    while ($fila = mysqli_fetch_assoc($resultado)) {
        $datos[] = $fila;
    }

    echo json_encode($datos);

    mysqli_close($conexion);
}
?>