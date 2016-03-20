<?php
	//ini_set("display_errors", 1);
	//ini_set('display_startup_errors', 1);
	//error_reporting(E_ALL);
	include 'db.php';
	header('Content-type: application/json');
		
	function getScores(){
		$query = "SELECT * FROM SnakeScores ORDER BY Score DESC";
		$result = doDBQuery($query);
		
		$output = array();
		while($result && $r = mysql_fetch_assoc($result)){
			array_push($output, convertResultToArray($r));
		}
		
		return(json_encode($output, JSON_FORCE_OBJECT));
	}
	
	function convertResultToArray($r){
		$arr = array(
				"name" => $r['Name'],
				"score" => $r['Score']
			);
		return($arr);
	}
	
	echo getScores();
?>