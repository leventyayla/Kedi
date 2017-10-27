<?php
 
include("ayar.php");
$current_index = $_POST['current_index']; 
$metin = $_POST['metin'];
$return_arr = array();

$fetch = mysql_query("SELECT e.etkinlik_id, e.baslik, e.aciklama, e.etkinlik_turu, e.url, e.baslangic_saati, e.sure, e.baslangic_tarihi, e.son_duzenleme, e.qr_str, k.isim AS  'kulup_isim', k.url AS 'kulup_url', (SELECT AVG(puan) FROM yorum WHERE etkinlik_id = e.etkinlik_id) AS 'puan' 
FROM etkinlik e LEFT JOIN kulup k ON e.kulup_id = k.kulup_id 
WHERE ((e.baslik LIKE '%$metin%') OR (e.aciklama LIKE '%$metin%')) AND e.onay = 1
ORDER BY e.etkinlik_id DESC 
LIMIT $current_index, 3"); 

while ($row = mysql_fetch_array($fetch, MYSQL_ASSOC)) {

    $row_array['etkinlik_id'] = $row['etkinlik_id'];
    $row_array['baslik'] = $row['baslik'];
    $row_array['aciklama'] = strip_tags($row['aciklama']);
	$row_array['etkinlik_turu'] = $row['etkinlik_turu'];
	$row_array['url'] = $row['url'];
	$row_array['baslangic_saati'] = $row['baslangic_saati'];
	$row_array['sure'] = $row['sure'];
	$row_array['baslangic_tarihi'] = $row['baslangic_tarihi'];
	$row_array['son_duzenleme'] = $row['son_duzenleme'];
	$row_array['qr_str'] = $row['qr_str'];
	$row_array['kulup_isim'] = $row['kulup_isim'];
	$row_array['kulup_url'] = $row['kulup_url'];
	$row_array['puan'] = $row['puan'];

    array_push($return_arr,$row_array);

}

echo json_encode($return_arr,JSON_UNESCAPED_UNICODE);

mysql_close($conn);

?>