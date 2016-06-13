<?php
//TODO mover sql a model
	if($_SERVER['REQUEST_METHOD'] != 'POST'){
		echo "lol";
		exit(403);
	}
	
	include("stuff/utils.php");
	include("stuff/model.php");
	$res = ["ok"=>false, "msg"=>" no idea"];
	
	try{
		$uid = intval($_POST['uid']);
		$utk = $_POST['utk'];
		$iid = intval($_POST['iid']);
		
		$cat  = intval(get($_POST['cat'], 0));
		
		//verify user
		check(logged($uid, $utk), "Usuario no logueado");
		
		$sth = $conn->prepare('DELETE FROM issues where uid = ? and id = ?;'); //el and uid asegura que sea el propio
		$sth->bindParam(1, $uid, PDO::PARAM_INT);
		$sth->bindParam(2, $iid, PDO::PARAM_INT);
		
		check($sth->execute(), "Error adding issue: ".$sth->errorCode().": ".$sth->errorInfo() );
		check($sth->rowCount()> 0, "No se pudo eliminar el issue");
		
		$res = [ 'ok'=>true, "msg" => ''];
	}catch(Exception $e) {
		$res = [ "ok"=> false, "msg" => $e->getMessage()];
	}

	echo json_encode($res);
	return;
?>