<?php
 
include("ayar.php");
$ogr_no = $_POST['ogr_no']; 
$response = array();

$result = mysql_query("DELETE FROM bildirimler WHERE ogr_no=$ogr_no");

if ($result) {
    $response["durum"] = true;
    $response["mesaj"] = "Başarılı bir şekilde silindi";

    echo json_encode($response,JSON_UNESCAPED_UNICODE);

} else {
    $response["durum"] = false;
    $response["mesaj"] = "Sunucu bildirim gösterme durumu için bilgilendirilemedi!";

    echo json_encode($response,JSON_UNESCAPED_UNICODE);
}

mysql_close($conn);

?>