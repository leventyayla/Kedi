<?php
 
include("ayar.php");
$etkinlik_id = $_POST['etkinlik_id']; 
$duyuru_id = $_POST['duyuru_id']; 
$ogr_no = $_POST['ogr_no']; 
$response = array();

if ($duyuru_id && $ogr_no){
	
	$result = mysql_query("SELECT ogr_no,duyuru_id FROM favoriler WHERE duyuru_id=$duyuru_id AND ogr_no=$ogr_no");

	if (mysql_num_rows($result)) {
		$response["durum"] = true;

		echo json_encode($response,JSON_UNESCAPED_UNICODE);

	} else {
		$response["durum"] = false;

		echo json_encode($response,JSON_UNESCAPED_UNICODE);
	}
}else if ($etkinlik_id && $ogr_no){
	
	$result = mysql_query("SELECT ogr_no,etkinlik_id FROM favoriler WHERE etkinlik_id=$etkinlik_id AND ogr_no=$ogr_no");

	if (mysql_num_rows($result)) {
		$response["durum"] = true;

		echo json_encode($response,JSON_UNESCAPED_UNICODE);

	} else {
		$response["durum"] = false;

		echo json_encode($response,JSON_UNESCAPED_UNICODE);
	}
}

mysql_close($conn);
?>