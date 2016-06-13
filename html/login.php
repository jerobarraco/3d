<?php
	header('Content-Type: application/json');
	
	include("stuff/utils.php");
	include("stuff/model.php");
	
	$data = [];
	
	try{
		$atk = $_POST["atk"];
		$fid = $_POST["fid"];
		
		check(isset($atk), "....");

		$js = json_decode(file_get_contents("https://graph.facebook.com/me?fields=id&access_token=$atk"), true);

		check(isset($js['id']), "Invalid access_token ".$js);
		
		check($js['id'] == $fid, "Invalid fid ".$fid);
		
		//get or create user 
		session_start();
		//get user
		$sth = $conn->prepare('SELECT * FROM users WHERE fid = ?;');
		$sth->bindParam(1, $fid, PDO::PARAM_STR, 255);
		
		check( $sth->execute(), "Error getting user: ".$sth->errorCode().": ".$sth->errorInfo() );
		
		//get uid
		$uid = -1;
		if ($sth->rowCount() > 0){
			$result = $sth->fetch(PDO::FETCH_ASSOC);
			$uid = $result['id'];
		}else{//no user, create it
			$sth = $conn->prepare('INSERT INTO users (fid, ftk, utk, rep) values (?, "", "", 0);');
			$sth->bindParam(1, $fid, PDO::PARAM_STR, 255);
			
			check(!$sth->execute(), "Error creating user: ".$sth->errorCode().": ".$sth->errorInfo() );
			$uid = $conn->lastInsertId();
		}
		
		$utk = session_id();
		//save utk and atk
		
		$sth = $conn->prepare('UPDATE users SET ftk = ?, utk = ? where id = ? ;');//we could add "and fid =?" but logically this couldnt happen in this code flow
		$sth->bindParam(1, $atk, PDO::PARAM_STR, 255);
		$sth->bindParam(2, $utk, PDO::PARAM_STR, 255);
		$sth->bindParam(3, $uid, PDO::PARAM_INT);
		
		check($sth->execute(), "Error updating user: ".$sth->errorCode().": ".$sth->errorInfo() );
		$data = [ "uid"=>$uid, "utk"=>$utk, "fid"=>$fid, "fbdata"=>$js ];
		$msg = "";
		$ok = true;
	}catch(Exception $e){
		$ok = false;
		$msg = $e->getMessage();
		$data = [ ];
	}

	putJS($data, $ok, $msg);
?>