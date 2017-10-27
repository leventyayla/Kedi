<?php
 
include("ayar.php");
$current_index = $_POST['current_index'];
$return_arr = array();

$fetch = mysql_query("SELECT duyuru.duyuru_id, duyuru.baslik, duyuru.aciklama, duyuru.tarih, kulup.isim as 'kulup_isim' 
FROM duyuru LEFT JOIN kulup ON duyuru.kulup_id=kulup.kulup_id 
WHERE duyuru.onay = 1
ORDER BY duyuru.duyuru_id DESC 
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