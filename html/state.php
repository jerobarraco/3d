<?php
	if($_SERVER['REQUEST_METHOD'] != 'POST'){
		echo "lol";
		exit(403);
	}
	
	include("stuff/utils.php");
	include("stuff/model.php");
	
	try{
		$uid = intval($_POST['uid']);
		$utk = $_POST['utk'];
		$iid = intval($_POST['iid']);
		
		$state  = intval($_POST['state']);
		
		check(logged($uid, $utk), "User not logged");
		
		$issue = check(getIssue($iid), "No issue");
		check($issue['uid']==$uid, "Not your issue");
		
		check(setIssueState($iid, $state), "Can't change the status of issue $issue to $state");
		
		$ok = true;
		$res = [];
	}catch(Exception $e) {
		$msg = $e->getMessage();
		$ok = false;
		
	}
	putJS($res, $ok, $msg);
?>