package tez.levent.feyyaz.kedi.models;

/**
 * Created by Levent on 14.05.2017.
 */

public class Favori {
    public static final int DUYURU = 0;
    public static final int ETKINLIK = 1;

    private String etkinlik_adi,etkinlik_aciklama,etkinlik_url,etkinlik_tarih,etkinlik_qr_str,
            etkinlik_kulup_isim,etkinlik_son_duzenleme,etkinlik_kulup_url,etkinlik_puan;
    private int etkinlik_id,etkinlik_sure;

    private int duyuru_id;
    private String duyuru_adi,duyuru_aciklama,duyuru_tarih,duyuru_kulup_isim,duyuru_kulup_url;

    private String tarih;

    private int type;

    public Favori(int etkinlik_id,String etkinlik_adi,String etkinlik_aciklama,String etkinlik_url,String etkinlik_tarih,
           int etkinlik_sure,String etkinlik_kulup_isim,String etkinlik_kulup_url,String etkinlik_son_duzenleme,
           String etkinlik_qr_str,String etkinlik_puan,String tarih){
        type = ETKINLIK;
        this.etkinlik_id=etkinlik_id;
        this.etkinlik_adi=etkinlik_adi;
        this.etkinlik_aciklama=etkinlik_aciklama;
        this.etkinlik_url=etkinlik_url;
        this.etkinlik_tarih=etkinlik_tarih;
        this.etkinlik_sure=etkinlik_sure;
        this.etkinlik_kulup_isim=etkinlik_kulup_isim;
        this.etkinlik_kulup_url=etkinlik_kulup_url;
        this.etkinlik_son_duzenleme=etkinlik_son_duzenleme;
        this.etkinlik_qr_str=etkinlik_qr_str;
        this.etkinlik_puan=etkinlik_puan;
        this.tarih=tarih;
    }

    public Favori(int duyuru_id,String duyuru_adi,String duyuru_aciklama,String duyuru_tarih,String duyuru_kulup_isim,
           String duyuru_kulup_url, String tarih){
        type = DUYURU;
        this.duyuru_id=duyuru_id;
        this.duyuru_adi=duyuru_adi;
        this.duyuru_aciklama=duyuru_aciklama;
        this.duyuru_tarih=duyuru_tarih;
        this.duyuru_kulup_isim=duyuru_kulup_isim;
        this.duyuru_kulup_url=duyuru_kulup_url;
        this.tarih=tarih;
    }

    public String getEtkinlik_adi() {
        return etkinlik_adi;
    }

    public String getEtkinlik_aciklama() {
        return etkinlik_aciklama;
    }

    public String getEtkinlik_url() {
        return etkinlik_url;
    }

    public String getEtkinlik_tarih() {
        return etkinlik_tarih;
    }

    public String getEtkinlik_qr_str() {
        return etkinlik_qr_str;
    }

    public String getEtkinlik_kulup_isim() {
        return etkinlik_kulup_isim;
    }

    public String getEtkinlik_son_duzenleme() {
        return etkinlik_son_duzenleme;
    }

    public String getEtkinlik_kulup_url() {
        return etkinlik_kulup_url;
    }

    public String getEtkinlik_puan() {
        return etkinlik_puan;
    }

    public int getEtkinlik_id() {
        return etkinlik_id;
    }

    public int getEtkinlik_sure() {
        return etkinlik_sure;
    }

    public int getDuyuru_id() {
        return duyuru_id;
    }

    public String getDuyuru_adi() {
        return duyuru_adi;
    }

    public String getDuyuru_aciklama() {
        return duyuru_aciklama;
    }

    public String getDuyuru_tarih() {
        return duyuru_tarih;
    }

    public String getDuyuru_kulup_isim() {
        return duyuru_kulup_isim;
    }

    public String getDuyuru_kulup_url() {
        return duyuru_kulup_url;
    }

    public String getTarih() {
        return tarih;
    }

    public int getType() {
        return type;
    }
}
