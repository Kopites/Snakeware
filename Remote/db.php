<?php
	function doDBQuery($query){
		$username="sql1300465";
		$password="bqJkFIiu";
		$database="lochnagar.abertay.ac.uk";

		mysql_connect($database,$username,$password);
		@mysql_select_db($username) or die(json_encode(array(), JSON_FORCE_OBJECT));
		$sql = $query;
		$result = mysql_query($sql) or die(json_encode(array(), JSON_FORCE_OBJECT));
		mysql_close();
		
		return($result);
	}
?>