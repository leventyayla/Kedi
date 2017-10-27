<?php
 
include("ayar.php");
$current_index = $_POST['current_index'];
$ogr_no = $_POST['ogr_no']; 

$return_arr = array();

$fetch = mysql_query("SELECT d.duyuru_id, d.baslik, d.aciklama, d.tarih, k.isim as 'kulup_isim'
from duyuru d
LEFT join kulup k ON k.kulup_id = d.kulup_id
WHERE d.onay = 1 AND d.kulup_id IN (SELECT kulup_id from takip_edilen Where ogr_no = $ogr_no)
ORDER BY d.duyuru_id DESC
LIMIT $current_index, 6");
	
while ($row = mysql_fetch_array($fetch, MYSQL_ASSOC)) {
	
	$row_array['duyuru_id'] = $row['duyuru_id'];
    $row_array['baslik'] = $row['baslik'];
    $row_array['aciklama'] = strip_tags($row['aciklama']);
	$row_array['tarih'] = $row['tarih'];
	$row_array['kulup_isim'] = $row['kulup_isim'];
    array_push($return_arr,$row_array);
}

echo json_encode($return_arr,JSON_UNESCAPED_UNICODE);
mysql_close($conn);
	
?>