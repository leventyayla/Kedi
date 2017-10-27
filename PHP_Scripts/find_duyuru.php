<?php
 
include("ayar.php");
$current_index = $_POST['current_index'];
$metin = $_POST['metin'];
$return_arr = array();

$fetch = mysql_query("SELECT d.duyuru_id, d.baslik, d.aciklama, d.tarih, k.isim as 'kulup_isim' 
FROM duyuru d LEFT JOIN kulup k ON d.kulup_id=k.kulup_id 
WHERE ((d.baslik LIKE '%$metin%') OR (d.aciklama LIKE '%$metin%')) AND d.onay = 1
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