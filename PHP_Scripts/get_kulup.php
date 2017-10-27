<?php
 
include("ayar.php");
$ogr_no = $_POST['ogr_no']; 

$return_arr = array();

$getir = mysql_query("SELECT k.kulup_id, k.isim, k.hakkimizda, k.url, k.son_duzenleme, t.ogr_no
FROM kulup k
LEFT JOIN takip_edilen t ON t.kulup_id = k.kulup_id
AND t.ogr_no = $ogr_no
ORDER BY isim"); 

while ($row = mysql_fetch_array($getir, MYSQL_ASSOC)) {

	$row_array['kulup_id'] = $row['kulup_id'];

    $row_array['isim'] = $row['isim'];

    $row_array['hakkimizda'] = strip_tags($row['hakkimizda']);

    $row_array['url'] = $row['url'];
	
	$row_array['son_duzenleme'] = $row['son_duzenleme'];
	
	if ($row['ogr_no'] == $ogr_no) {
		$row_array['takip_durumu'] = true;
	}else{
		$row_array['takip_durumu'] = false;
	}

    array_push($return_arr,$row_array);
}

echo json_encode($return_arr,JSON_UNESCAPED_UNICODE);

mysql_close($conn);

?>