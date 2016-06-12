<?php
	header('Content-Type: application/json');
	
	$data = array(
		-2 => 'Abiertos',
		-1 => 'Todos',
		0 => 'Nuevo', 
		1 => 'Resuelto',
		2 => 'En trabajo'
	);
	
	print(json_encode(array("ok"=>true, "error"=>"", "data"=>$data),
		JSON_PARTIAL_OUTPUT_ON_ERROR|JSON_HEX_TAG|JSON_HEX_AMP|JSON_HEX_APOS|JSON_HEX_QUOT));
	//print(json_last_error());
	exit();
?>