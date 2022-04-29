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
$resultado[] = array('resultado' => false);
$stmt = mysqli_query($con,"SELECT * FROM Imagenes WHERE usuario='$Email'");
if (mysqli_num_rows($stmt)==0){
	mysqli_query($con,"INSERT INTO Imagenes (usuario,imagen) VALUES ('$Email','$image')");
    $resultado[] = array('resultado' => true);
}else{
	mysqli_query($con,"UPDATE Imagenes SET imagen='$image' WHERE usuario='$Email'");
	$resultado[] = array('resultado' => true);
}



echo json_encode($resultado);
mysqli_close($con)

?>