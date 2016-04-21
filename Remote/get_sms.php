<?php
	//ini_set("display_errors", 1);
	//ini_set('display_startup_errors', 1);
	//error_reporting(E_ALL);
	include_once 'db.php';
	header('Content-type: application/json');

	function getSMSFromPhone($id){
		$id = mysql_escape_string(htmlspecialchars($id));
		$query = "SELECT * FROM SMS WHERE DeviceID = ".$id;
		if(isset($_GET['outgoing']) && $_GET['outgoing'] == true){
			$query .= ' AND Outgoing = '.$_GET['outgoing'];
		}
		$query .= ' ORDER BY time DESC';
		$result = doDBQuery($query);

		$output = array();
		while($result && $r = mysql_fetch_assoc($result)){
			array_push($output, convertResultToArray($r));
		}

		return(json_encode($output, JSON_FORCE_OBJECT));
	}

	function convertResultToArray($r){
		$arr = array(
				"deviceID" => $r['DeviceID'],
				"participant" => $r['Participant'],
				"outgoing" => ($r['Outgoing'] ? true : false),
				"time" => $r['Time'],
				"message" => $r['Message'],
				"location" => array(
								"latitude" => $r['Latitude'],
								"longitude" => $r['Longitude']
								)
			);
		return($arr);
	}

	if(isset($_GET['deviceID'])){
		echo getSMSFromPhone($_GET['deviceID']);
	}
?>
