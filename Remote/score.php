<?php
	ini_set("display_errors", 1);
	ini_set('display_startup_errors', 1);
	error_reporting(E_ALL);
	include 'db.php';

	if($_SERVER['REQUEST_METHOD'] == 'POST'){
		$name = mysql_escape_string(htmlspecialchars($_POST['name']));
		$score = mysql_escape_string(htmlspecialchars($_POST['score']));
		
		if($name == null || $score == null){
			die();
		}
		
		$sql = "INSERT INTO SnakeScores (Name, Score) VALUES ('".$name."', '".$score."');";
		
		doDBQuery($sql);
		echo("Success");
	}	
?>