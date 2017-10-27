<?php
 
include("ayar.php");
$etkinlik_id = $_POST['etkinlik_id']; 
$ogr_no = $_POST['ogr_no']; 
$yorum = $_POST['yorum']; 
$puan = $_POST['puan']; 
$response = array();

$result = mysql_query("INSERT INTO yorum(ogr_no, etkinlik_id, icerik, puan) VALUES ('$ogr_no','$etkinlik_id','$yorum',$puan)") or die(mysql_error());

if ($result) {
    $response["durum"] = true;
    $response["mesaj"] = "Başarılı bir şekilde eklendi";
	$response["id"] = mysql_insert_id();
	
    echo json_encode($response,JSON_UNESCAPED_UNICODE);

} else {
    $response["durum"] = false;
    $response["mesaj"] = "Sorun oldu";

    echo json_encode($response,JSON_UNESCAPED_UNICODE);

}

mysql_close($conn);
?>