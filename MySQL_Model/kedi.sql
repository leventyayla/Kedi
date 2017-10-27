-- phpMyAdmin SQL Dump
-- version 4.7.0
-- https://www.phpmyadmin.net/
--
-- Anamakine: 127.0.0.1
-- Üretim Zamanı: 27 Eki 2017, 13:57:22
-- Sunucu sürümü: 10.1.25-MariaDB
-- PHP Sürümü: 5.6.31

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Veritabanı: `kedi`
--

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `bildirimler`
--

CREATE TABLE `bildirimler` (
  `ogr_no` bigint(13) UNSIGNED NOT NULL,
  `etkinlik_id` int(13) DEFAULT NULL,
  `duyuru_id` int(13) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `duyuru`
--

CREATE TABLE `duyuru` (
  `duyuru_id` int(13) NOT NULL,
  `baslik` varchar(100) NOT NULL,
  `aciklama` varchar(500) NOT NULL,
  `tarih` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `onay` tinyint(4) NOT NULL,
  `kulup_id` int(13) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `etkinlik`
--

CREATE TABLE `etkinlik` (
  `etkinlik_id` int(13) NOT NULL,
  `baslik` varchar(100) NOT NULL,
  `aciklama` varchar(7000) NOT NULL,
  `etkinlik_turu` varchar(30) NOT NULL,
  `url` varchar(200) NOT NULL,
  `baslangic_saati` varchar(5) NOT NULL,
  `bitis_saati` varchar(5) NOT NULL,
  `yeri` varchar(100) NOT NULL,
  `sure` int(4) NOT NULL,
  `baslangic_tarihi` varchar(20) NOT NULL,
  `baslangic_gunu` varchar(20) NOT NULL,
  `olusturuldugu_tarih` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `son_duzenleme` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `kulup_id` int(13) NOT NULL,
  `onay` tinyint(1) NOT NULL DEFAULT '0',
  `qr_str` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `favoriler`
--

CREATE TABLE `favoriler` (
  `ogr_no` bigint(13) UNSIGNED NOT NULL,
  `etkinlik_id` int(13) DEFAULT NULL,
  `duyuru_id` int(13) DEFAULT NULL,
  `tarih` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `katilimlar`
--

CREATE TABLE `katilimlar` (
  `ogr_no` bigint(13) UNSIGNED NOT NULL,
  `etkinlik_id` int(13) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `kulup`
--

CREATE TABLE `kulup` (
  `kulup_id` int(13) NOT NULL,
  `isim` varchar(50) NOT NULL,
  `url` varchar(200) NOT NULL,
  `son_duzenleme` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `kadi` varchar(31) NOT NULL,
  `sifre` varchar(31) NOT NULL,
  `yetkili_adi` varchar(50) DEFAULT NULL,
  `yetkili_email` varchar(50) DEFAULT NULL,
  `iletisim_no` varchar(30) DEFAULT NULL,
  `danisman_adi` varchar(50) DEFAULT NULL,
  `danisman_email` varchar(50) DEFAULT NULL,
  `hakkimizda` varchar(5000) DEFAULT NULL,
  `misyon` varchar(5000) DEFAULT NULL,
  `vizyon` varchar(5000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `ogrenci`
--

CREATE TABLE `ogrenci` (
  `ogr_no` bigint(13) UNSIGNED NOT NULL,
  `isim` varchar(20) NOT NULL,
  `soyisim` varchar(30) NOT NULL,
  `email` varchar(40) DEFAULT NULL,
  `parola` varchar(50) NOT NULL,
  `url` varchar(200) DEFAULT NULL,
  `son_duzenleme` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `durum` varchar(20) DEFAULT NULL,
  `kod` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `takip_edilen`
--

CREATE TABLE `takip_edilen` (
  `ogr_no` bigint(13) UNSIGNED NOT NULL,
  `kulup_id` int(13) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `yorum`
--

CREATE TABLE `yorum` (
  `id` int(13) NOT NULL,
  `ogr_no` bigint(13) UNSIGNED NOT NULL,
  `etkinlik_id` int(13) NOT NULL,
  `icerik` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `tarih` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `puan` int(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dökümü yapılmış tablolar için indeksler
--

--
-- Tablo için indeksler `bildirimler`
--
ALTER TABLE `bildirimler`
  ADD UNIQUE KEY `ogr_no_2` (`ogr_no`,`etkinlik_id`),
  ADD UNIQUE KEY `ogr_no_3` (`ogr_no`,`duyuru_id`),
  ADD KEY `ogr_no` (`ogr_no`),
  ADD KEY `etkinlik_id` (`etkinlik_id`),
  ADD KEY `duyuru_id` (`duyuru_id`);

--
-- Tablo için indeksler `duyuru`
--
ALTER TABLE `duyuru`
  ADD PRIMARY KEY (`duyuru_id`),
  ADD KEY `kulup_id` (`kulup_id`);

--
-- Tablo için indeksler `etkinlik`
--
ALTER TABLE `etkinlik`
  ADD PRIMARY KEY (`etkinlik_id`),
  ADD KEY `kulup_id` (`kulup_id`);

--
-- Tablo için indeksler `favoriler`
--
ALTER TABLE `favoriler`
  ADD UNIQUE KEY `ogr_no_2` (`ogr_no`,`etkinlik_id`),
  ADD UNIQUE KEY `ogr_no_3` (`ogr_no`,`duyuru_id`),
  ADD KEY `ogr_no` (`ogr_no`),
  ADD KEY `etkinlik_id` (`etkinlik_id`),
  ADD KEY `duyuru_id` (`duyuru_id`);

--
-- Tablo için indeksler `katilimlar`
--
ALTER TABLE `katilimlar`
  ADD UNIQUE KEY `ogr_no_2` (`ogr_no`,`etkinlik_id`),
  ADD KEY `ogr_no` (`ogr_no`),
  ADD KEY `etkinlik_id` (`etkinlik_id`);

--
-- Tablo için indeksler `kulup`
--
ALTER TABLE `kulup`
  ADD PRIMARY KEY (`kulup_id`);

--
-- Tablo için indeksler `ogrenci`
--
ALTER TABLE `ogrenci`
  ADD PRIMARY KEY (`ogr_no`);

--
-- Tablo için indeksler `takip_edilen`
--
ALTER TABLE `takip_edilen`
  ADD UNIQUE KEY `ogr_no_2` (`ogr_no`,`kulup_id`),
  ADD KEY `kulup_id` (`kulup_id`),
  ADD KEY `ogr_no` (`ogr_no`);

--
-- Tablo için indeksler `yorum`
--
ALTER TABLE `yorum`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `ogr_no_2` (`ogr_no`,`etkinlik_id`),
  ADD KEY `ogr_no` (`ogr_no`),
  ADD KEY `etkinlik_id` (`etkinlik_id`);

--
-- Dökümü yapılmış tablolar için AUTO_INCREMENT değeri
--

--
-- Tablo için AUTO_INCREMENT değeri `duyuru`
--
ALTER TABLE `duyuru`
  MODIFY `duyuru_id` int(13) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;
--
-- Tablo için AUTO_INCREMENT değeri `etkinlik`
--
ALTER TABLE `etkinlik`
  MODIFY `etkinlik_id` int(13) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=47;
--
-- Tablo için AUTO_INCREMENT değeri `kulup`
--
ALTER TABLE `kulup`
  MODIFY `kulup_id` int(13) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;
--
-- Tablo için AUTO_INCREMENT değeri `yorum`
--
ALTER TABLE `yorum`
  MODIFY `id` int(13) NOT NULL AUTO_INCREMENT;
--
-- Dökümü yapılmış tablolar için kısıtlamalar
--

--
-- Tablo kısıtlamaları `bildirimler`
--
ALTER TABLE `bildirimler`
  ADD CONSTRAINT `bildirimler_ibfk_1` FOREIGN KEY (`ogr_no`) REFERENCES `ogrenci` (`ogr_no`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `bildirimler_ibfk_2` FOREIGN KEY (`etkinlik_id`) REFERENCES `etkinlik` (`etkinlik_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `bildirimler_ibfk_3` FOREIGN KEY (`duyuru_id`) REFERENCES `duyuru` (`duyuru_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Tablo kısıtlamaları `duyuru`
--
ALTER TABLE `duyuru`
  ADD CONSTRAINT `duyuru_ibfk_1` FOREIGN KEY (`kulup_id`) REFERENCES `kulup` (`kulup_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Tablo kısıtlamaları `etkinlik`
--
ALTER TABLE `etkinlik`
  ADD CONSTRAINT `etkinlik_ibfk_1` FOREIGN KEY (`kulup_id`) REFERENCES `kulup` (`kulup_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Tablo kısıtlamaları `favoriler`
--
ALTER TABLE `favoriler`
  ADD CONSTRAINT `favoriler_ibfk_2` FOREIGN KEY (`etkinlik_id`) REFERENCES `etkinlik` (`etkinlik_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `favoriler_ibfk_3` FOREIGN KEY (`duyuru_id`) REFERENCES `duyuru` (`duyuru_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `ogr_no_4` FOREIGN KEY (`ogr_no`) REFERENCES `ogrenci` (`ogr_no`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Tablo kısıtlamaları `katilimlar`
--
ALTER TABLE `katilimlar`
  ADD CONSTRAINT `katilimlar_ibfk_1` FOREIGN KEY (`etkinlik_id`) REFERENCES `etkinlik` (`etkinlik_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `ogr_no` FOREIGN KEY (`ogr_no`) REFERENCES `ogrenci` (`ogr_no`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Tablo kısıtlamaları `takip_edilen`
--
ALTER TABLE `takip_edilen`
  ADD CONSTRAINT `ogr_no_2` FOREIGN KEY (`ogr_no`) REFERENCES `ogrenci` (`ogr_no`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `takip_edilen_ibfk_2` FOREIGN KEY (`kulup_id`) REFERENCES `kulup` (`kulup_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Tablo kısıtlamaları `yorum`
--
ALTER TABLE `yorum`
  ADD CONSTRAINT `ogr_no_3` FOREIGN KEY (`ogr_no`) REFERENCES `ogrenci` (`ogr_no`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `yorum_ibfk_2` FOREIGN KEY (`etkinlik_id`) REFERENCES `etkinlik` (`etkinlik_id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
