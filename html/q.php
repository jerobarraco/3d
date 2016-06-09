<?php
	//include("stuff/pconn.php");
	// DONE agregar fotos
	// DONE agregar categoria
	
	if (!isset($_GET["s"], $_GET["w"], $_GET["n"], $_GET["e"])) {
		echo "error";
		exit(403);
	}
	
	include("stuff/pconn.php");
	// explode southwest corner into two variables
	$s = doubleval($_GET["s"]);//lat
	$w = doubleval($_GET["w"]);
	$n = doubleval($_GET['n']);//lat
	$e = doubleval($_GET['e']);//long = east
	$q = '';
	
	try{
		$uid = intval(get($_GET['uid'], -1));
		$utk = get($_GET['utk'], "");
		isLogged($uid, $utk);
	}catch(Exception $e){
		$uid = -1;
	}
	
	if($w <= $e){
		// doesn't cross the antimeridian
		//$rows = CS50::query("SELECT * FROM places WHERE ? <= latitude AND latitude <= ? AND (? <= longitude AND longitude <= ?) GROUP BY country_code, place_name, admin_code1 ORDER BY RAND() LIMIT 10", $sw_lat, $ne_lat, $sw_lng, $ne_lng);
		$q = "SELECT *, uid = ? as mine FROM issues WHERE (lat between ? AND ?) AND (lon between ? AND ?)";
	} else {
		$q = "SELECT *, uid = ? as mine FROM issues WHERE (lat between ? AND ?) AND NOT (lon BETWEEN ? AND ? )";
		// crosses the antimeridian
		//$rows = CS50::query("SELECT * FROM places WHERE ? <= latitude AND latitude <= ? AND (? <= longitude OR longitude <= ?) GROUP_BY country_code, place_name, admin_code1 ORDER BY RAND() LIMIT 10", $sw_lat, $ne_lat, $sw_lng, $ne_lng);
	}
	
	$pars = array($uid, $s, $n, $w, $e);
	$cat = intval(get($_GET['cat'], -1));
	if($cat >= 0 ){
		$q .= " AND cat = ?";
		$pars[] = $cat;
	}
	
	$q .= " ORDER BY state ASC, score DESC, date DESC LIMIT 50";
    $stmt = $conn->prepare($q);
    
	//print_r($pars);
	$stmt->execute($pars);
	//var_dump($stmt->debugDumpParams());
	
	//echo ("<br> rows <br>");
	$res = array();
	
	foreach ($stmt as $row) {
		$res[] = array(
			"idn"=>intval($row['id']), "mine" =>(bool) $row['mine'], "score"=>intval($row['score']),
			"cat"=>intval($row['cat']), "state"=>intval($row['state']),
			"lat"=>doubleval($row['lat']), "lon"=>doubleval($row['lon']),
			"descr"=>$row['descr']
		);
	}
	
	
	header('Content-Type: application/json');

	print(
		json_encode(
			array("ok"=>true, "error"=>"", "data"=>$res),
			JSON_PARTIAL_OUTPUT_ON_ERROR|JSON_HEX_TAG|JSON_HEX_AMP|JSON_HEX_APOS|JSON_HEX_QUOT
		)
	);
	//print(json_last_error());
	exit();
?>