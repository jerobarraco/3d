<?php
	// DONE agregar fotos
	// DONE agregar categoria
	// TODO refactorizar, mover esos queries a model
	
	if (!isset($_GET["s"], $_GET["w"], $_GET["n"], $_GET["e"])) {
		echo "error";
		exit(403);
	}
	
	include("stuff/utils.php");
	include("stuff/model.php");
	// explode southwest corner into two variables
	$s = doubleval($_GET["s"]);//lat
	$w = doubleval($_GET["w"]);
	$n = doubleval($_GET['n']);//lat
	$e = doubleval($_GET['e']);//long = east
	$q = '';
	$uid = -1;
	try{
		$uid = intval(get($_GET['uid'], -1));
		$utk = get($_GET['utk'], "");
		if (!logged($uid, $utk)){
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
		$data = array();
		
		foreach ($stmt as $row) {
			$data[] = array(
				"iid"=>intval($row['id']), "mine" =>(bool) $row['mine'], "score"=>intval($row['score']),
				"cat"=>intval($row['cat']), "state"=>intval($row['state']),
				"lat"=>doubleval($row['lat']), "lon"=>doubleval($row['lon']),
				"descr"=>$row['descr']
			);
		}
		$ok = true;
		$msg = "";
	}catch(Exception $e){
		$data = [];
		$ok = false;
		$msg = $e->getMessage();
	}
	putJS($data, $ok, $msg);
?>