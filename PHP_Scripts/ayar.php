<?php 
 
$host="localhost";

$db="DATABASE_ADI";

$user="DATABASE_KULLANICI";

$pass="DATABASE_KULLANICI_SIFRE";

$conn=@mysql_connect($host,$user,$pass) or die("Mysql Bağlanamadı");

 
mysql_select_db($db,$conn) or die("Veritabanına Bağlanılamadı");

mysql_set_charset('utf8',$conn);

?>