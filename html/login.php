<?php
	header('Content-Type: application/json');
	
	include("stuff/pconn.php");
	$res = ["ok"=>false, "msg"=>" no idea", "data"=>[]];
	//try{
		$atk = $_POST["atk"];
		if (!isset($atk)){
			throw new Exception ("....");
		}
		
		$js = json_decode(file_get_contents("https://graph.facebook.com/me?fields=id&access_token=$atk"), true);
		if( !isset($js['id']) ){
			throw new Exception("Invalid access_token ".$js);
		}
		session_start();    
		$res  = ["ok"=>true, "error"=>"", "data"=>[ "uid"=>$js['id'], "utk"=>session_id(), "fib"=>$_POST['fib'], "atk"=>$atk , "fbdata"=>$js]];
	//}catch(Exception $e) {
	//	$res = [ "ok"=> false, "msg" => $e->getMessage(), "idn"=>-1];
	//}

	echo json_encode($res,
			JSON_PARTIAL_OUTPUT_ON_ERROR|JSON_HEX_TAG|JSON_HEX_AMP|JSON_HEX_APOS|JSON_HEX_QUOT);
	return;
?>