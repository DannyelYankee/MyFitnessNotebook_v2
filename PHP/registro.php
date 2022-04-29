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


$Email = $_POST["user"];
$Contraseña = $_POST["password"];
$token = $_POST["token"];
$hashed_contra=password_hash($Contraseña, PASSWORD_DEFAULT);
$stmt=mysqli_query($con,"SELECT * FROM Usuario WHERE Email='$Email'");
if (mysqli_num_rows ($stmt)==0){
	mysqli_query($con,"INSERT INTO Usuario (Email,Contraseña,token) VALUES ('$Email','$hashed_contra','$token')");
	$resultado[] = array('resultado' => true);
}else{
	$resultado[] = array('resultado' => false);
}
echo json_encode($resultado);
mysqli_close($con);

?>
