<?php
 
include("ayar.php");
$etkinlik_id = $_POST['etkinlik_id']; 

$return_arr = array();

$getir = mysql_query("SELECT y.id, o.isim, o.soyisim, o.url, y.icerik, y.tarih, y.puan
FROM yorum y
LEFT JOIN ogrenci o ON o.ogr_no = y.ogr_no
WHERE y.etkinlik_id = $etkinlik_id
ORDER BY y.tarih DESC"); 

while ($row = mysql_fetch_array($getir, MYSQL_ASSOC)) {

	$row_array['id'] = $row['id'];
    $row_array['isim'] = $row['isim'];
    $row_array['soyisim'] = $row['soyisim'];
    $row_array['url'] = $row['url'];
	$row_array['icerik'] = $row['icerik'];
	$row_array['tarih'] = $row['tarih'];
	$row_array['puan'] = $row['puan'];

    array_push($return_arr,$row_array);
}

echo json_encode($return_arr,JSON_UNESCAPED_UNICODE); 
mysql_close($conn);
 
?>