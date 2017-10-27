<?php
 
include("ayar.php");

$kadi = $_POST['k_adi']; 
$sifre = $_POST['k_sif']; 

$sql_check = mysql_query("select * from ogrenci where ogr_no='".$kadi."' and parola='".$sifre."';") or die(mysql_error()); 


if(mysql_num_rows($sql_check))  {
	echo "{ \"status\": true }";
}else{
	echo "{ \"status\": false }";
}

mysql_close($conn);
?>