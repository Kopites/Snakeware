<?php
	ini_set("display_errors", 1);
	ini_set('display_startup_errors', 1);
	error_reporting(E_ALL);

	if($_SERVER['REQUEST_METHOD'] == 'POST' || true){
		$deviceID = mysql_escape_string(htmlspecialchars($_POST['deviceID']));
		$participant = mysql_escape_string(htmlspecialchars($_POST['participant']));
		$outgoing = mysql_escape_string(htmlspecialchars($_POST['outgoing']));
		$time = mysql_escape_string(htmlspecialchars($_POST['time']));
		$message = mysql_escape_string(htmlspecialchars($_POST['message']));
		$latitude = mysql_escape_string(htmlspecialchars($_POST['latitude']));
		$longitude = mysql_escape_string(htmlspecialchars($_POST['longitude']));
		
		if($deviceID == null || $participant == null){
			die();
		}
		
		$sql = 'INSERT INTO SMS (DeviceID, Participant, Outgoing, Time, Message, Latitude, Longitude) VALUES';
		$sql .= "(".$deviceID.", '".$participant."', ".$outgoing.", FROM_UNIXTIME('".$time."'), '".$message."', ".$latitude.", ".$longitude.")";
		
		doDBQuery($sql);
		echo("Success");
	}	
	
	function doDBQuery($query){
		$username="sql1300465";
		$password="bqJkFIiu";
		$database="lochnagar.abertay.ac.uk";

		mysql_connect($database,$username,$password);
		@mysql_select_db($username) or die( "Unable to select database");
		mysql_query($query);
		mysql_close();
	}
?>