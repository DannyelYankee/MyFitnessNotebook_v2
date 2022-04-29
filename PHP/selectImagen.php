<?php
$DB_SERVER="localhost";
$DB_USER="Xdjuape001";
$DB_PASS="*EAYh2J9y"; 
$DB_DATABASE="Xdjuape001_usuarios";

$con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);

if (mysqli_connect_errno($con)){
        echo 'Error de conexion: ' . mysqli_connect_error();
		$resultado[] = array('resultado' => false);
        exit();
}
$image=$_POST['imagen'];
$Email = $_POST['user'];

$stmt = mysqli_query($con,"SELECT * FROM Imagenes WHERE usuario='$Email'");
if (mysqli_num_rows ($stmt)>0){	
	$fila = mysqli_fetch_row($stmt);
	$resultado[] = array('resultado'=> $fila[1]);
}else{
	$resultado[] = array('resultado'=> false);
}
echo json_encode($resultado);
mysqli_close($con)

?>