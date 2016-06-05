<?php
	header('Content-Type: application/json');
	
	include("stuff/pconn.php");
	$res = ["ok"=>false, "msg"=>" no idea", "data"=>[]];
	try{
		$atk = $_POST["atk"];
		$fid = $_POST["fid"];
		
		if (!isset($atk)){
			throw new Exception ("....");
		}
		
		$js = json_decode(file_get_contents("https://graph.facebook.com/me?fields=id&access_token=$atk"), true);
		if( !isset($js['id']) ){
			throw new Exception("Invalid access_token ".$js);
		}
		
		if ($js['id'] != $fid){
			throw new Exception("Invalid fid ".$fid);
		}
		
		//get or create user 
		session_start();
		//get user
		$sth = $conn->prepare('SELECT * FROM users WHERE fid = ?;');
		$sth->bindParam(1, $fid, PDO::PARAM_STR, 255);
		
		if (!$sth->execute()){
			throw new Exception("Error getting user: ".$sth->errorCode().": ".$sth->errorInfo() );
		}
		
		//get uid
		$uid = -1;
		if ($sth->rowCount() > 0){
			$result = $sth->fetch(PDO::FETCH_ASSOC);
			$uid = $result['id'];
		}else{//no user, create it
			$sth = $conn->prepare('INSERT INTO users (fid, ftk, utk, rep) values (?, "", "", 0);');
			$sth->bindParam(1, $fid, PDO::PARAM_STR, 255);
			
			if (!$sth->execute()){
				throw new Exception("Error creating user: ".$sth->errorCode().": ".$sth->errorInfo() );
			}
			$uid = $conn->lastInsertId();
		}
		
		$utk = session_id();
		//save utk and atk
		
		$sth = $conn->prepare('UPDATE users SET ftk = ?, utk = ? where id = ? ;');//we could add "and fid =?" but logically this couldnt happen in this code flow
		$sth->bindParam(1, $atk, PDO::PARAM_STR, 255);
		$sth->bindParam(2, $utk, PDO::PARAM_STR, 255);
		$sth->bindParam(3, $uid, PDO::PARAM_INT);
		
		if (!$sth->execute()){
			throw new Exception("Error updating user: ".$sth->errorCode().": ".$sth->errorInfo() );
		}
		
		$res = ["ok"=> true, "msg"=>"", "data"=>[ "uid"=>$uid, "utk"=>$utk, "fid"=>$fid, "fbdata"=>$js]];
	}catch(Exception $e){
		$res = [ "ok"=> false, "msg" => $e->getMessage(), "idn"=>-1];
	}

	echo json_encode($res, JSON_PARTIAL_OUTPUT_ON_ERROR|JSON_HEX_TAG|JSON_HEX_AMP|JSON_HEX_APOS|JSON_HEX_QUOT);
	return;
?>