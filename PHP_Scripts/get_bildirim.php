<?php
 
include("ayar.php");
$ogr_no = $_POST['ogr_no']; 

$return_arr = array();

$getir = mysql_query("SELECT e.etkinlik_id, 
e.baslik as 'etkinlik_adi', e.aciklama as 'etkinlik_aciklama', e.etkinlik_turu, e.url as 'ekinlik_url', e.baslangic_saati as 'etkinlik_baslangic_saati', e.sure as 'etkinlik_sure', e.baslangic_tarihi as 'etkinlik_baslangic_tarihi', ke.isim as 'etkinlik_kulup_isim', ke.url as 'etkinlik_kulup_url', e.son_duzenleme as 'etkinlik_son_duzenleme', e.qr_str as 'etkinlik_qr_str', (SELECT AVG(puan) FROM yorum WHERE etkinlik_id = e.etkinlik_id) AS 'etkinlik_puan',
d.duyuru_id, d.baslik as 'duyuru_adi', d.aciklama as 'duyuru_aciklama', d.tarih as 'duyuru_tarih', kd.isim as 'duyuru_kulup_isim', kd.url as 'duyuru_kulup_url' 
FROM bildirimler b 
Left join etkinlik e on e.etkinlik_id = b.etkinlik_id 
left join duyuru d on d.duyuru_id = b.duyuru_id 
left join kulup ke on e.kulup_id = ke.kulup_id 
left join kulup kd on d.kulup_id = kd.kulup_id 
WHERE ogr_no = $ogr_no AND (e.onay = 1 OR d.onay = 1)"); 

if ($getir){
	while ($row = mysql_fetch_array($getir, MYSQL_ASSOC)) {
	
	if ($row['etkinlik_id'] != null && $row['duyuru_id'] == null){
		$etkinlik_array['etkinlik_id'] = $row['etkinlik_id'];
		$etkinlik_array['duyuru_id'] = $row['duyuru_id'];
		$etkinlik_array['etkinlik_adi'] = $row['etkinlik_adi'];
		$etkinlik_array['etkinlik_aciklama'] = strip_tags($row['etkinlik_aciklama']);
		$etkinlik_array['etkinlik_turu'] = $row['etkinlik_turu'];
		$etkinlik_array['ekinlik_url'] = $row['ekinlik_url'];
		$etkinlik_array['etkinlik_baslangic_saati'] = $row['etkinlik_baslangic_saati'];
		$etkinlik_array['etkinlik_sure'] = $row['etkinlik_sure'];
		$etkinlik_array['etkinlik_baslangic_tarihi'] = $row['etkinlik_baslangic_tarihi'];
		$etkinlik_array['etkinlik_kulup_isim'] = $row['etkinlik_kulup_isim'];
		$etkinlik_array['etkinlik_kulup_url'] = $row['etkinlik_kulup_url'];
		$etkinlik_array['etkinlik_son_duzenleme'] = $row['etkinlik_son_duzenleme'];
		$etkinlik_array['etkinlik_qr_str'] = $row['etkinlik_qr_str'];
		$etkinlik_array['etkinlik_puan'] = $row['etkinlik_puan'];
		
		array_push($return_arr,$etkinlik_array);
	}
	if ($row['duyuru_id'] != null && $row['etkinlik_id'] == null){
		$duyuru_array['etkinlik_id'] = $row['etkinlik_id'];
		$duyuru_array['duyuru_id'] = $row['duyuru_id'];
		$duyuru_array['duyuru_adi'] = $row['duyuru_adi'];
		$duyuru_array['duyuru_aciklama'] = strip_tags($row['duyuru_aciklama']);
		$duyuru_array['duyuru_tarih'] = $row['duyuru_tarih'];
		$duyuru_array['duyuru_kulup_isim'] = $row['duyuru_kulup_isim'];
		$duyuru_array['duyuru_kulup_url'] = $row['duyuru_kulup_url'];
		
		array_push($return_arr,$duyuru_array);
	}
}
	if (empty($return_arr)){
		echo "0";
	}else{
		echo json_encode($return_arr,JSON_UNESCAPED_UNICODE);
	}
}

mysql_close($conn);

?>