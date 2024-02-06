<?php

/**
 * Script PHP para manejar los datos recibidos del formulario y almacenarlos en la base de datos.
 *
 * Este script espera recibir los siguientes parámetros a través de una solicitud POST:
 * - nombre: Nombre de la ciudad.
 * - tiempo: Estado del tiempo en la ciudad.
 * - temperatura: Temperatura en la ciudad.
 * - humedad: Humedad en la ciudad.
 * - viento: Velocidad del viento en la ciudad.
 * - fecha: Fecha de registro.
 * - hora: Hora de registro.
 */

if(isset($_POST["nombre"]) && isset($_POST["tiempo"]) && isset($_POST["temperatura"]) && isset($_POST["humedad"]) && isset($_POST["viento"]) && isset($_POST["fecha"]) && isset($_POST["hora"])){

    $nombre = $_POST["nombre"];
    $tiempo = $_POST["tiempo"];
    $temperatura = $_POST["temperatura"];
    $humedad = $_POST["humedad"];
    $viento = $_POST["viento"];
    $fecha = $_POST["fecha"];
    $hora = $_POST["hora"];
    $servidor = "localhost";
    $usuario = "root";
    $password = "";
    $dbname = "clima";
    $conexion = mysqli_connect($servidor, $usuario, $password, $dbname);

    if (!$conexion) {
        echo "Error en la conexion a MySQL: ".mysqli_connect_error();
        exit();
    }

    $sql = "INSERT INTO ciudad (nombre,tiempo,temperatura,humedad,viento,fecha,hora) VALUES ('".$nombre."','".$tiempo."','".$temperatura."','".$humedad."','".$viento."','".$fecha."','".$hora."')";

    if (mysqli_query($conexion, $sql)) {
        echo "Registro insertado correctamente.";
    } else {
        echo "Error: " . $sql . "<br>" . mysqli_error($conexion);
    }

}
?>
