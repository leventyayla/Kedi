<?php
 
include("ayar.php");
$etkinlik_id = $_POST['etkinlik_id']; 
$duyuru_id = $_POST['duyuru_id']; 
$ogr_no = $_POST['ogr_no']; 
$response = array();

if ($duyuru_id && $ogr_no){
	
	$result = mysql_query("DELETE FROM favoriler WHERE duyuru_id=$duyuru_id AND ogr_no=$ogr_no");

	if ($result) {
		$response["durum"] = true;
		$response["mesaj"] = "Başarılı bir şekilde çıkarıldı";

		echo json_encode($response,JSON_UNESCAPED_UNICODE);

	} else {
		$response["durum"] = false;
		$response["mesaj"] = "Sorun oldu";

		echo json_encode($response,JSON_UNESCAPED_UNICODE);
	}
}else if ($etkinlik_id && $ogr_no){
	
	$result = mysql_query("DELETE FROM favoriler WHERE etkinlik_id=$etkinlik_id AND ogr_no=$ogr_no");

	if ($result) {
		$response["durum"] = true;
		$response["mesaj"] = "Başarılı bir şekilde çıkarıldı";

		echo json_encode($response,JSON_UNESCAPED_UNICODE);

	} else {
		$response["durum"] = false;
		$response["mesaj"] = "Sorun oldu";

		echo json_encode($response,JSON_UNESCAPED_UNICODE);
	}
}

mysql_close($conn); 
?>