<?php
ini_set('post_max_size', '2M');
ini_set('upload_max_filesize', '2M');
// TODO set max upload effectively 
// TODO move sql shit to model

	if($_SERVER['REQUEST_METHOD'] == 'GET'){
		echo "lol";
		exit(403);
	}
	
	include("stuff/utils.php");
	include("stuff/model.php");
	
	$res = ["ok"=>false, "msg"=>" no idea", "iid"=>-1];
	
	try{
		$lat = $_POST['lat'];
		$lon = $_POST['lon'];
		$acc = $_POST['acc'];
		$uid = intval($_POST['uid']);
		$utk = $_POST['utk'];
		
		if (!isset($_POST['descr']) || $_POST['descr'] == ""){ 
			throw new Exception("La descripcion no puede estar vacia");
		}
		
		$descr = $_POST['descr'];
		$cat  = intval(get($_POST['cat'], 0));
		
		//verify user
		$sth = $conn->prepare('Select * from users where id = ? and utk = ? LIMIT 1;');
		$sth->bindParam(1, $uid, PDO::PARAM_INT);
		$sth->bindParam(2, $utk, PDO::PARAM_STR, 255);
		
		check($sth->execute(),  "Error authenticating: ".$sth->errorCode().": ".$sth->errorInfo() );
		check($sth->rowCount()>= 1, "User not logged : ".$sth->errorCode().": ".$sth->errorInfo() );
		
		$sth = $conn->prepare('INSERT INTO issues (uid, lat, lon, descr, acc, cat, state) VALUES (?, ?, ?, ?, ?, ?, 0);');
		$sth->bindParam(1, $uid, PDO::PARAM_INT);
		$sth->bindParam(2, $lat, PDO::PARAM_INT);
		$sth->bindParam(3, $lon, PDO::PARAM_INT);
		$sth->bindParam(4, $descr, PDO::PARAM_STR, 255);
		$sth->bindParam(5, $acc, PDO::PARAM_STR); //there's no PDO for decimal.... well done ph
		$sth->bindParam(6, $cat, PDO::PARAM_INT);
		
		check($sth->execute(), "Error adding issue: ".$sth->errorCode().": ".$sth->errorInfo() );
		
		$idn = $conn->lastInsertId();
		$fname = "s/i/".$idn.".jpg";
		if($_FILES["photo"]){
			$fbin = $_FILES["photo"]["tmp_name"];
			if ($fbin && (getimagesize($fbin) !== false)) {
				move_uploaded_file($fbin, $fname);
			}
		}else if($f64 = $_POST['f64']){
			if (startsWith($f64, 'data:')){
				$f64 = explode(',', $f64)[1];
			}
			//file_put_contents($fname.".b64", $f64);
			file_put_contents($fname, base64_decode($f64));
			//file_put_contents($fname, file_get_contents($f64));
		}else{
			//no photo
		}
		$res = [ 'ok'=>true, "msg" => 'it is all dandy', "iid"=>intval($idn)];
	}catch(Exception $e) {
		$res = [ "ok"=> false, "msg" => $e->getMessage(), "iid"=>-1];
	}

	echo json_encode($res);
	return;
?>