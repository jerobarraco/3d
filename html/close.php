<?php
	if($_SERVER['REQUEST_METHOD'] != 'POST'){
		echo "lol";
		exit(403);
	}
	
	include("stuff/pconn.php");
	$res = ["ok"=>false, "msg"=>" no idea"];
	
	try{
		$uid = intval($_POST['uid']);
		$utk = $_POST['utk'];
		$iid = intval($_POST['iid']);
		
		$cat  = intval(get($_POST['cat'], 0));
		
		//verify user
		isLogged($uid, $utk);
		
		$sth = $conn->prepare('DELETE FROM issues where uid = ? and id = ?;'); //el and uid asegura que sea el propio
		$sth->bindParam(1, $uid, PDO::PARAM_INT);
		$sth->bindParam(2, $iid, PDO::PARAM_INT);
		
		if (!$sth->execute()){
			throw new Exception("Error adding issue: ".$sth->errorCode().": ".$sth->errorInfo() );
		}
		if ($sth->rowCount()<= 0 ){
			throw new Exception("No se pudo eliminar el issue");
		}
		
		$res = [ 'ok'=>true, "msg" => ''];
	}catch(Exception $e) {
		$res = [ "ok"=> false, "msg" => $e->getMessage()];
	}

	echo json_encode($res);
	return;
?>