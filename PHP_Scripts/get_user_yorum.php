<?php
 
include("ayar.php");

$ogr_no = $_POST['ogr_no']; 
$etkinlik_id = $_POST['etkinlik_id']; 
$return_arr = array();

$katilim = mysql_query("SELECT * FROM katilimlar WHERE ogr_no=$ogr_no AND etkinlik_id=$etkinlik_id");

$sql_check = mysql_query("SELECT *,(SELECT etkinlik_id FROM katilimlar WHERE etkinlik_id=y.etkinlik_id) AS 'katilim' FROM yorum y WHERE y.ogr_no = $ogr_no AND y.etkinlik_id = $etkinlik_id");

if(mysql_num_rows($katilim)) {
	
	if(mysql_num_rows($sql_check)) {
		
		while ($row = mysql_fetch_array($sql_check, MYSQL_ASSOC)) {
			$row_array['yorum_yapti_mi'] = true;
			$row_array['id'] = $row['id'];
			$row_array['icerik'] = $row['icerik'];
			$row_array['tarih'] = $row['tarih'];
			$row_array['puan'] = $row['puan'];
			$row_array['katilim'] = true;
			array_push($return_arr,$row_array);
		}
		echo json_encode($return_arr,JSON_UNESCAPED_UNICODE);
		
	}else{
		$row_array1['katilim'] = true;
		$row_array1['yorum_yapti_mi'] = false;
		array_push($return_arr,$row_array1);
		echo json_encode($return_arr,JSON_UNESCAPED_UNICODE);
	}
}else {
	$row_array2['katilim'] = false;
	array_push($return_arr,$row_array2);
	echo json_encode($return_arr,JSON_UNESCAPED_UNICODE);
}

mysql_close($conn);
?>