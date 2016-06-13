<?php
	include("stuff/utils.php");
	$data = array(
		-2 => 'Todos',
		-1 => 'Abiertos',
		0 => 'Nuevo', 
		1 => 'Resuelto',
		2 => 'En trabajo'
	);
	putJS($data);
?>