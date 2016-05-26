<?php
	$servername = "localhost";
	$username = "grapotco_ddd";
	$password = "123;456";
	$database = "grapotco_ddd";

//try 
	$conn = new PDO("mysql:host=$servername;dbname=$database", $username, $password);
    // set the PDO error mode to exception
	$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
	$conn->query('SET CHARACTER SET utf8');
  //  echo "Connected successfully"; 
  //}
//	catch(PDOException $e)  { echo "Connection failed: " . $e->getMessage(); }

	/*// Check connection
	if (!$conn) {
		die("Connection failed: " . mysqli_connect_error());
	}
	echo "Connected successfully"; 
	*/
	
	function get(&$var, $default=null) {
    	return isset($var) ? $var : $default;
	}
	
	function startsWith($haystack, $needle){
		$length = strlen($needle);
		return (substr($haystack, 0, $length) === $needle);
	}
	function endsWith($haystack, $needle){
		$length = strlen($needle);
		if ($length == 0) {
			return true;
		}
		return (substr($haystack, -$length) === $needle);
	}

?>