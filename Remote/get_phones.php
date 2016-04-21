<?php
	//ini_set("display_errors", 1);
	//ini_set('display_startup_errors', 1);
	//error_reporting(E_ALL);
	include_once 'db.php';
	header('Content-type: application/json');

	function getPhones(){
		$query = "SELECT SMS.DeviceID FROM SMS, PhoneCalls WHERE SMS.DeviceID = PhoneCalls.DeviceID GROUP BY SMS.DeviceID";
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
			);
		return($arr);
	}

	echo getPhones();
?>
