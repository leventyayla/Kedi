package tez.levent.feyyaz.kedi.models;

/**
 * Created by Levent on 6.12.2016.
 */

public class Kulup {
    private String  isim, hakkimizda,url,son_duzenleme;
    private int id;
    private Boolean isFollow;

    public Kulup(int id, String isim, String hakkimizda, String url, String son_duzenleme, Boolean isFollow) {
        this.id=id;
        this.isim = isim;
        this.hakkimizda = hakkimizda;
        this.url = url;
        this.son_duzenleme=son_duzenleme;
        this.isFollow=isFollow;
    }

    @Override
    public String toString() {
        return isim;
    }

    public String getIsim() {
        return isim;
    }

    public void setFollowing(Boolean state) {
        isFollow = state;
    }

    public Boolean getFollowing() {
        return isFollow;
    }

    public int getId() {
        return id;
    }

    public String getHakkimizda() {
        return hakkimizda;
    }

    public String getUrl() {
        return url;
    }

    public String getSon_duzenleme() {
        return son_duzenleme;
    }
}
