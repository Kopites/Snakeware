<?php
	ini_set("display_errors", 1);
	ini_set('display_startup_errors', 1);
	error_reporting(E_ALL);
	include 'db.php';

	if($_SERVER['REQUEST_METHOD'] == 'POST'){
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
?>