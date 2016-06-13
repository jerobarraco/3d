<?php
//include before model. i have no idea why yet
function check($c, $msg=''){
	if (!$c){
		$msg = $msg.': '.mysql_error();
		throw new Exception($msg);
	}
	return $c;
}
	
function get(&$var, $default=null) {
	return isset($var) ? $var : $default;
}

function startsWith($haystack, $needle){
	$length = strlen($needle);
	return (substr($haystack, 0, $length) === $needle);
}

function endsWith($haystack, $needle){
	$length = strlen($needle);
	if ($length == 0) {
		return true;
	}
	return (substr($haystack, -$length) === $needle);
}

$ok = false;
$msg = "unknown";
$data = [];

function putJS($pdata, $pok=true, $pmsg=""){
	header('Content-Type: application/json');
	print(
		json_encode(
			array("ok"=>$pok, "msg"=>$pmsg, "data"=>$pdata),
			JSON_PARTIAL_OUTPUT_ON_ERROR|JSON_HEX_TAG|JSON_HEX_AMP|JSON_HEX_APOS|JSON_HEX_QUOT
		)
	);
	exit();
}

?>
