<?php
	if($_SERVER['REQUEST_METHOD'] != 'POST'){
		echo "lol";
		exit(403);
	}
	
	include("stuff/utils.php");
	include("stuff/model.php");
	
	$res = ["ok"=>false, "msg"=>"no idea"];
	
	try{
		$uid = intval($_POST['uid']);
		$utk = $_POST['utk'];
		$iid = intval($_POST['iid']);
		
		$status  = intval($_POST['status']);
		
		check(logged($uid, $utk), "User not logged");
		
		$issue = check(getIssue($iid), "No issue");
		check($issue['uid']==$uid, "Not your issue");
		
		check(setIssueState($iid, $status), "Can't change the status of issue $issue to $status");
		
		$res = [ 'ok'=> true, "msg" => ''];
	}catch(Exception $e) {
		$res = [ "ok"=> false, "msg" => $e->getMessage()];
	}

	echo json_encode($res);
	return;
?>