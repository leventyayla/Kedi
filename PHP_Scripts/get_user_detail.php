<?php
 
include("ayar.php");
$ogr_no = $_POST['ogr_no']; 

$return_arr = array();

$fetch = mysql_query("SELECT isim, soyisim, url, son_duzenleme FROM ogrenci WHERE ogr_no='".$ogr_no."';"); 

while ($row = mysql_fetch_array($fetch, MYSQL_ASSOC)) {

    $row_array['isim'] = $row['isim'];

    $row_array['soyisim'] = $row['soyisim'];

    $row_array['url'] = $row['url'];
	
	$row_array['son_duzenleme'] = $row['son_duzenleme'];

    array_push($return_arr,$row_array);

}

echo json_encode($return_arr,JSON_UNESCAPED_UNICODE);

mysql_close($conn);

?>