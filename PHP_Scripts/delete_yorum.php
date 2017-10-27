<?php
 
include("ayar.php");
$yorum_id = $_POST['yorum_id']; 
$response = array();

$result = mysql_query("DELETE FROM yorum WHERE id = $yorum_id");

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