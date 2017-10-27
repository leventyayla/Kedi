<?php
 
include("ayar.php");
$kulup_id = $_POST['kulup_id']; 
$ogr_no = $_POST['ogr_no']; 
$response = array();

$result = mysql_query("INSERT INTO takip_edilen (kulup_id , ogr_no) VALUES ( $kulup_id, $ogr_no )");

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