<?php
 
include("ayar.php");

$ogr_no = $_POST['ogr_no']; 
$ad = $_POST['ad']; 
$soyad = $_POST['soyad']; 
$sifre = $_POST['sif']; 

$sql = "INSERT INTO ogrenci (ogr_no,isim,soyisim,parola) VALUES (".$ogr_no.",'".$ad."','".$soyad."','".$sifre."')";

$retval = mysql_query( $sql, $conn );

if(! $retval ) {
    echo "{ \"status\": false }";
}else{
	echo "{ \"status\": true }";
}

mysql_close($conn);

?>