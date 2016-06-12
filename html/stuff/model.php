<?php
include("pconn.php");

function setIssueState($iid, $state){
	global $conn;
	$sth = $conn->prepare('UPDATE issues set state = ? where id = ? LIMIT 1;');
	$sth->bindParam(1, $state, PDO::PARAM_INT);
	$sth->bindParam(2, $iid, PDO::PARAM_INT);
	if (!$sth->execute()){
		return false;
	}
	if ($sth->rowCount()<1){
		return false;
	}
	return true;
}

function getIssue($iid){
	global $conn;
	$sth = $conn->prepare('SELECT * from issues where id = ? LIMIT 1;');
	$sth->bindParam(1, $iid, PDO::PARAM_INT);
	if (!$sth->execute()){
		return false;
	}
	
	return $sth->fetch(PDO::FETCH_ASSOC);
}

function logged($uid, $utk){
	global $conn;
	$sth = $conn->prepare('SELECT * from users where id = ? and utk = ? LIMIT 1;');
	$sth->bindParam(1, $uid, PDO::PARAM_INT);
	$sth->bindParam(2, $utk, PDO::PARAM_STR, 255);
	
	if (!$sth->execute()){
		return false;
		//throw new Exception("Error authenticating: ".$sth->errorCode().": ".$sth->errorInfo() );
	}
	
	if ($sth->rowCount()<1){
		return false;
	//throw new Exception("User not logged : ".$sth->errorCode().": ".$sth->errorInfo() );
	}
	return true;
}
?>