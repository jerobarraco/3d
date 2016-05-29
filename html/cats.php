<?php
	header('Content-Type: application/json');
	
	$data = array(
		0 => 'Sin categoría', 
		1 => 'Animales',
		2 => 'Ambientales', 
		3 => 'Policiales', 
		4 => 'Legales', 
		5 => 'Servicios', 
		6 => 'Culturales',
		7 => 'Negocios',
		8 => 'Emergencias'
	);
	
	print(json_encode(array("ok"=>true, "error"=>"", "data"=>$data),
		JSON_PARTIAL_OUTPUT_ON_ERROR|JSON_HEX_TAG|JSON_HEX_AMP|JSON_HEX_APOS|JSON_HEX_QUOT));
	//print(json_last_error());
	exit();
?>