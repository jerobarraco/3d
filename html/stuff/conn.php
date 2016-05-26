<?php
	$servername = "localhost";
	$username = "grapotco_ddd";
	$password = "123;456";
	$database = "grapotco_ddd";

	// Create connection
	$conn = mysqli_connect($servername, $username, $password, $database);

	/*// Check connection
	if (!$conn) {
		die("Connection failed: " . mysqli_connect_error());
	}
	echo "Connected successfully"; 
	*/

?>