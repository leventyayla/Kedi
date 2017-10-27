<?php
 
include("ayar.php");
$etkinlik_id = $_POST['etkinlik_id']; 
$ogr_no = $_POST['ogr_no']; 
$response = array();

$result = mysql_query("INSERT INTO  katilimlar (ogr_no, etkinlik_id) VALUES ($ogr_no, $etkinlik_id)");

if ($result) {
    $response["durum"] = true;
    $response["mesaj"] = "Etkinliğe katılımınız başarıyla gerçekleşti!";

    echo json_encode($response,JSON_UNESCAPED_UNICODE);

} else {
    $response["durum"] = false;
    $response["mesaj"] = "Katılımınız sunucuya kayıt edilirken sorun oluştu!";

    echo json_encode($response,JSON_UNESCAPED_UNICODE);

}

mysql_close($conn);
?>