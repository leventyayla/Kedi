package tez.levent.feyyaz.kedi.models;

/**
 * Created by Levent on 12.12.2016.
 */

public class Duyuru {
    private String baslik;
    private String aciklama;
    private String tarih;
    private String kulup_adi;
    private int id;

    public Duyuru(int id, String baslik, String aciklama, String tarih, String kulup_adi) {
        this.id=id;
        this.baslik = baslik;
        this.aciklama = aciklama;
        this.tarih = tarih;
        this.kulup_adi=kulup_adi;
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

    public String getKulup_adi() {
        return kulup_adi;
    }

    public String getAciklama() {
        return aciklama;
    }

    public String getTarih() {
        return tarih;
    }
}