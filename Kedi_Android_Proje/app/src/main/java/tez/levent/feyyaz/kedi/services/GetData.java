package tez.levent.feyyaz.kedi.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import tez.levent.feyyaz.kedi.R;
import tez.levent.feyyaz.kedi.activities.SplashScreen;
import tez.levent.feyyaz.kedi.activities.details.DuyuruActivity;
import tez.levent.feyyaz.kedi.activities.details.EtkinlikActivity;
import tez.levent.feyyaz.kedi.models.Duyuru;
import tez.levent.feyyaz.kedi.models.Etkinlik;

public class GetData extends Service {

    String KULUP_IMAGE_DIR = SplashScreen.URL + "kulup_logo/";
    String ETKINLIK_IMAGE_DIR = SplashScreen.URL + "etkinlik_foto/";
    Context context;
    int no = 0, bildirimSayisi = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("GetData Servisi","Çalıştı!");

        context = getBaseContext();
        SharedPreferences sharedPref = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String ogr_no = sharedPref.getString("k_adi",null);

        getNotificaion(ogr_no);
    }

    private void getNotificaion(String ogr_no){
        if (ogr_no == null) stopSelf();

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        params.put("ogr_no", ogr_no);
        client.post(SplashScreen.URL + "android/get_bildirim.php",params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull final byte[] bildirimData) {

                String data = new String(bildirimData);
                if (!data.equals("0")){
                    try {
                        JSONArray bildirimArray = new JSONArray(data);
                        bildirimSayisi = bildirimArray.length();

                        for (int i = 0; i < bildirimSayisi; i++) {
                            JSONObject temp = bildirimArray.getJSONObject(i);

                            if (!temp.getString("etkinlik_id").equals("null")){
                                String puan = null;
                                if (!temp.getString("etkinlik_puan").equals("null")){
                                    puan = temp.getString("etkinlik_puan");
                                }
                                Etkinlik etkinlik=new Etkinlik(temp.getInt("etkinlik_id"),
                                        temp.getString("etkinlik_adi"),
                                        temp.getString("etkinlik_aciklama").trim(),
                                        temp.getString("ekinlik_url"),
                                        temp.getInt("etkinlik_sure"),
                                        temp.getString("etkinlik_baslangic_tarihi")+" "+temp.getString("etkinlik_baslangic_saati"),
                                        temp.getString("etkinlik_son_duzenleme"),
                                        temp.getString("etkinlik_kulup_isim"),
                                        temp.getString("etkinlik_qr_str"),
                                        temp.getString("etkinlik_kulup_url"),
                                        puan);
                                String url = temp.getString("etkinlik_kulup_url");
                                resimGetir(etkinlik,null,url);

                            }else if (!temp.getString("duyuru_id").equals("null")){

                                Duyuru duyuru = new Duyuru(temp.getInt("duyuru_id"),
                                        temp.getString("duyuru_adi"),
                                        temp.getString("duyuru_aciklama").trim(),
                                        temp.getString("duyuru_tarih"),
                                        temp.getString("duyuru_kulup_isim"));
                                String url = temp.getString("duyuru_kulup_url");
                                resimGetir(null,duyuru,url);
                            }
                        }
                        Log.d("GetData Servisi","Bildirim verisi alındı!");

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        stopSelf();
                    }
                }else {
                    Log.d("GetData Servisi","Bildirim verisi 0 geldi servis kapanıyor.");
                    stopSelf();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                stopSelf();
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent ıntent) {
        return null;
    }

    private void resimGetir(@Nullable final Etkinlik etkinlik, @Nullable final Duyuru duyuru, String kulupLogoUrl){

        Glide.with(context)
                .load(KULUP_IMAGE_DIR + kulupLogoUrl)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .into(new SimpleTarget<Bitmap>(100,100) {
                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        Log.e("GetData Servisi","Kulüp bitmap dosyası indirilemedi");
                        stopSelf();
                    }

                    @Override
                    public void onResourceReady(final Bitmap kulupLogo, GlideAnimation glideAnimation) {
                        if (duyuru != null){
                            bildirimYolla(duyuru.getKulup_adi() + " yeni bir duyuru oluşturdu",
                                    duyuru.getBaslik(), null, duyuru, kulupLogo,null);
                        }else if (etkinlik != null){
                            Glide.with(context)
                                    .load(ETKINLIK_IMAGE_DIR + etkinlik.getURL())
                                    .asBitmap().centerCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .skipMemoryCache(true)
                                    .into(new SimpleTarget<Bitmap>(300,300) {
                                        @Override
                                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                            Log.e("GetData Servisi","Etkinlik bitmap dosyası indirilemedi");
                                            stopSelf();
                                        }

                                        @Override
                                        public void onResourceReady(Bitmap etkinlikGorsel, GlideAnimation glideAnimation) {
                                            bildirimYolla(etkinlik.getKulup_isim() + " yeni bir etkinlik oluşturdu",
                                                    etkinlik.getBaslik(), etkinlik, null, kulupLogo,etkinlikGorsel);
                                        }
                                    });
                        }
                    }
                });
        Log.d("GetData Servisi","Gerekli bitmap(ler) indirildi.");

    }

    private void bildirimYolla(String baslik,
                              String icerik,
                              @Nullable Etkinlik etkinlik, @Nullable Duyuru duyuru,
                              Bitmap kulupLogo,@Nullable Bitmap etkinlikGorsel){

        Intent i = new Intent();
        if (duyuru != null){
            i=new Intent(context, DuyuruActivity.class);
            i.putExtra("id",duyuru.getId());
            i.putExtra("kulup_adi",duyuru.getKulup_adi());
            i.putExtra("baslik",duyuru.getBaslik());
            i.putExtra("aciklama",duyuru.getAciklama());
            i.putExtra("tarih",duyuru.getTarih());
        }else if (etkinlik != null){
            i=new Intent(context, EtkinlikActivity.class);
            i.putExtra("id",etkinlik.getId());
            i.putExtra("baslik", etkinlik.getBaslik());
            i.putExtra("aciklama", etkinlik.getAciklama());
            i.putExtra("sure", etkinlik.getSure());
            i.putExtra("tarih", etkinlik.getTarih());
            i.putExtra("kulup_isim", etkinlik.getKulup_isim());
            i.putExtra("son_duzenleme", etkinlik.getSon_duzenleme());
            i.putExtra("qr_str", etkinlik.getQR_str());
            i.putExtra("url", etkinlik.getURL());
            String puan = etkinlik.getPuan();
            if (puan != null)
                i.putExtra("puan",puan);
            i.putExtra("kulup_url", etkinlik.getKulup_url());
        }
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, no, i, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.app_logo);
        if (etkinlikGorsel != null) { mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(etkinlikGorsel)); }
        mBuilder.setLargeIcon(kulupLogo);
        mBuilder.setContentTitle(baslik);
        mBuilder.setContentText(icerik);
        mBuilder.setAutoCancel(true);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setContentIntent(pi);

        //Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //mBuilder.setSound(soundUri);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try{
            nm.notify(no, mBuilder.build());
        }catch (SecurityException e){
            Log.e("GetData Servisi","Hata oldu ayar değişiyor! Hata: "+e.getMessage());
            mBuilder.setDefaults(Notification.DEFAULT_SOUND);
            nm.notify(no, mBuilder.build());
        }

        if (no == (bildirimSayisi-1)){
            bildirimleriTemizle();
        }else {
            no++;
        }
    }

    private void bildirimleriTemizle(){
        SharedPreferences sharedPref = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String k_adi = sharedPref.getString("k_adi",null);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        params.put("ogr_no", k_adi);
        client.post(SplashScreen.URL + "android/delete_bildirim.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull byte[] responseBody) {

                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    if (json.getBoolean("durum")){
                        Log.d("GetData Servisi","Sunucudan kullanıcının bildirimleri temizlendi!");
                        stopSelf();
                    }else {
                        Toast.makeText(getApplicationContext(),json.getString("mesaj"),Toast.LENGTH_LONG).show();
                        stopSelf();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    stopSelf();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, @NonNull Throwable error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                stopSelf();
            }
        });
    }
}
