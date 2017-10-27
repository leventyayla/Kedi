<?php
 
include("ayar.php");
$yorum_id = $_POST['yorum_id']; 
$icerik = $_POST['icerik']; 
$puan = $_POST['puan']; 
$response = array();

$result = mysql_query("UPDATE yorum SET icerik='$icerik', tarih=NOW(),puan='$puan' WHERE id=$yorum_id"); 

if ($result) {
    $response["durum"] = true;
    $response["mesaj"] = "Başarılı bir şekilde eklendi";

    echo json_encode($response,JSON_UNESCAPED_UNICODE);

} else {
    $response["durum"] = false;
    $response["mesaj"] = "Sorun oldu";

    echo json_encode($response,JSON_UNESCAPED_UNICODE);

}

mysql_close($conn);
?>