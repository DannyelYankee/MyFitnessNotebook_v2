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
echo $Email;
$stmt = mysqli_query($con,"SELECT * FROM Usuario");
while($row = mysqli_fetch_assoc($stmt)){
	$token[] = $row["token"];
}


$cabecera=array(
	'Authorization: key=AAAAHR9OLsY:APA91bHmZmJfGK1pjsGuT7NrIZL3ref1CFfHXPtdSFqwars5sVv_NkwXq9GLW0Y9AO4F2e_XJG-2yrTGa-SexGvl_Duz0fPVl8ui7U-AsRNs9k4AunisJ1cxFCv_e0WE0blOv7jS96Ih',
	'Content-Type: application/json'
);
	
	
$msg= array(
	'registration_ids' => $token,
	'notification' => array(
		'body' => 'Vete a entrenar o muere como un noname ',
		'title' => '¡Hora de ir a entrenar!',
		'icon' => 'ic_stat_ic_notification'
	)
);

$msgJSON = json_encode($msg);
		
$ch = curl_init(); #inicializar el handler de curl
#indicar el destino de la petición, el servicio FCM de google
curl_setopt( $ch, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send');
#indicar que la conexión es de tipo POST
curl_setopt( $ch, CURLOPT_POST, true );
#agregar las cabeceras
curl_setopt( $ch, CURLOPT_HTTPHEADER, $cabecera);
#Indicar que se desea recibir la respuesta a la conexión en forma de string
curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
#agregar los datos de la petición en formato JSON
curl_setopt( $ch, CURLOPT_POSTFIELDS, $msgJSON );
#ejecutar la llamada
$resultado= curl_exec( $ch );
#cerrar el handler de curl
curl_close( $ch );	

if (curl_errno($ch)) {
print curl_error($ch);
}
echo $resultado;

?>