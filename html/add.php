<?php
ini_set('post_max_size', '2M');
ini_set('upload_max_filesize', '2M');
//TODO add category
	if($_SERVER['REQUEST_METHOD'] == 'GET'){
		echo "lol";
		exit(403);
	}
	
	include("stuff/pconn.php");
	$res = ["ok"=>false, "msg"=>" no idea", "idn"=>-1];
	
	try{
		$lat = $_POST['lat'];
		$lon = $_POST['lon'];
		$acc = $_POST['acc'];
		
		if (!isset($_POST['descr']) || $_POST['descr'] == ""){ 
			throw new Exception("La descripcion no puede estar vacia");
		}
		
		$descr = $_POST['descr'];
		$cat  = intval(get($_POST['cat'], 0));
		
		$sth = $conn->prepare('INSERT INTO issues(lat, lon, descr, acc, cat, state) VALUES (?, ?, ?, ?, ?, 0);');
		$sth->bindParam(1, $lat, PDO::PARAM_INT);
		$sth->bindParam(2, $lon, PDO::PARAM_INT);
		$sth->bindParam(3, $descr, PDO::PARAM_STR, 255);
		$sth->bindParam(4, $acc, PDO::PARAM_STR); //there's no PDO for decimal.... well done ph
		$sth->bindParam(5, $cat, PDO::PARAM_INT);
		
		if (!$sth->execute()){
			throw new Exception("".$sth->errorCode().": ".$sth->errorInfo() );
		}
		
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
		$res = [ 'ok'=>true, "msg" => 'is all dandy', "idn"=>intval($idn)];
	}catch(Exception $e) {
		throw new Exception($e->getMessage());
		$res = [ "ok"=> false, "msg" => $e->getMessage(), "idn"=>-1];
	}

	echo json_encode($res);
	return;
?>