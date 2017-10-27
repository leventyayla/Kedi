package tez.levent.feyyaz.kedi.models;

import android.support.annotation.Nullable;

//Created by Levent on 11.12.2016.

public class Etkinlik {
    private String baslik,aciklama,url,tarih,qr_str,kulup_isim,son_duzenleme,kulup_url,puan;
    private int id,sure;

    public Etkinlik(int id, String baslik, String aciklama, String url, int sure, String tarih, String son_duzenleme,
                    String kulup_isim, String qr_str, String kulup_url,@Nullable String puan) {
        this.id=id;
        this.baslik = baslik;
        this.aciklama = aciklama;
        this.url=url;
        this.sure=sure;
        this.tarih = tarih;
        this.son_duzenleme=son_duzenleme;
        this.kulup_isim=kulup_isim;
        this.kulup_url = kulup_url;
        this.puan = puan;
        this.qr_str=qr_str;
    }

    @Override
    public String toString() {
        return baslik;
    }

    public String getBaslik() {
        return baslik;
    }

    public int getId() {
        return id;
    }

    public String getKulup_isim() {
        return kulup_isim;
    }

    public String getQR_str() {
        return qr_str;
    }

    public int getSure() {
        return sure;
    }

    public String getAciklama() {
        return aciklama;
    }

    public String getURL() {
        return url;
    }

    public String getTarih() {
        return tarih;
    }

    public String getSon_duzenleme() {
        return son_duzenleme;
    }

    public String getKulup_url() {
        return kulup_url;
    }

    public String getPuan() {
        return puan;
    }
}
