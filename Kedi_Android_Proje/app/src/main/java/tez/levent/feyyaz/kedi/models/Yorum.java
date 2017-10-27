package tez.levent.feyyaz.kedi.models;

/**
 * Created by Levent on 13.03.2017.
 */

public class Yorum {
    private int id;
    private int puan;
    private String isim;
    private String soyisim;
    private String url;
    private String icerik;
    private String tarih;

    public Yorum(int id, String isim,String soyisim,String url,String icerik,String tarih,int puan){
        this.id=id;
        this.isim=isim;
        this.soyisim=soyisim;
        this.url=url;
        this.puan=puan;
        this.icerik=icerik;
        this.tarih=tarih;
    }

    public int getId() {
        return id;
    }

    public int getPuan() {
        return puan;
    }

    public String getIsim() {
        return isim;
    }

    public String getSoyisim() {
        return soyisim;
    }

    public String getUrl() {
        return url;
    }

    public String getIcerik() {
        return icerik;
    }

    public String getTarih() {
        return tarih;
    }

}
