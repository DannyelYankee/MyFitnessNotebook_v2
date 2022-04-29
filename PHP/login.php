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
$Contraseña =$_POST["password"];
$resultado[] = array('resultado' => false);

$stmt = mysqli_query($con,"SELECT * FROM Usuario WHERE Email='$Email'");
if ($stmt->num_rows > 0){
	while($row = $stmt->fetch_assoc()){	
		$hashed_pass = $row['Contraseña'];
	}
	if(password_verify($Contraseña,$hashed_pass)){
		$resultado[] = array('resultado' => true);
	}
}
echo json_encode($resultado);
mysqli_close($con)

?>