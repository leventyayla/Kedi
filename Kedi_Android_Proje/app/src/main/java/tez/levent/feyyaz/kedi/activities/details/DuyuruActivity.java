package tez.levent.feyyaz.kedi.activities.details;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import tez.levent.feyyaz.kedi.R;
import tez.levent.feyyaz.kedi.fragments.KulupFragment;

import static tez.levent.feyyaz.kedi.activities.SplashScreen.URL;

public class DuyuruActivity extends AppCompatActivity {
    TextView baslik,aciklama,tarih,kulup_adi;
    int id;
    Boolean isInProcess=false;
    SparkButton favori;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duyuru);

        baslik=(TextView)findViewById(R.id.duyuru_baslik);
        aciklama=(TextView)findViewById(R.id.aciklama);
        tarih=(TextView)findViewById(R.id.tarih);
        kulup_adi=(TextView)findViewById(R.id.kulup_adi);
        favori = (SparkButton) findViewById(R.id.favori_spark);

        favori.setVisibility(View.INVISIBLE);
        id = getIntent().getIntExtra("id",0);
        kulup_adi.setText(getIntent().getStringExtra("kulup_adi"));
        baslik.setText(getIntent().getStringExtra("baslik"));
        aciklama.setText(getIntent().getStringExtra("aciklama"));
        tarih.setText(getIntent().getStringExtra("tarih"));
        getFavoriDurumu();

        favori.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if (!isInProcess){
                    if (buttonState) {
                        favoriEkle();
                    } else {
                        favoriCikar();
                    }
                }
            }
        });
    }

    private void getFavoriDurumu(){
        SharedPreferences sharedPref = getBaseContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String k_adi = sharedPref.getString("k_adi",null);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        params.put("duyuru_id", id);
        params.put("ogr_no", k_adi);
        client.post(URL + "android/get_favori_info.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull byte[] responseBody) {
                Animation myFadeInAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in);
                favori.setVisibility(View.VISIBLE);
                favori.startAnimation(myFadeInAnimation);

                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    if (json.getBoolean("durum")){
                        favori.setChecked(true);
                    }else {
                        favori.setChecked(false);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, @NonNull Throwable error) {
                Animation myFadeInAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in);
                favori.setVisibility(View.VISIBLE);
                favori.startAnimation(myFadeInAnimation);
                Toast.makeText(getBaseContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void favoriEkle(){
        isInProcess = true;
        SharedPreferences sharedPref = getBaseContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String k_adi = sharedPref.getString("k_adi",null);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        params.put("duyuru_id", id);
        params.put("ogr_no", k_adi);
        client.post(URL + "android/insert_favori.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull byte[] responseBody) {
                isInProcess=false;
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    if (json.getBoolean("durum")){
                        favori.setChecked(true);
                    }else {
                        favori.setChecked(false);
                        Toast.makeText(getBaseContext(),json.getString("mesaj"),Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    favori.setChecked(false);
                    Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, @NonNull Throwable error) {
                isInProcess=false;
                favori.setChecked(false);
                Toast.makeText(getBaseContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void favoriCikar(){
        isInProcess = true;
        SharedPreferences sharedPref = getBaseContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String k_adi = sharedPref.getString("k_adi",null);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        params.put("duyuru_id", id);
        params.put("ogr_no", k_adi);
        client.post(URL + "android/delete_favori.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull byte[] responseBody) {
                isInProcess=false;
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    if (json.getBoolean("durum")){
                        favori.setChecked(false);
                    }else {
                        favori.setChecked(true);
                        Toast.makeText(getBaseContext(),json.getString("mesaj"),Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    favori.setChecked(true);
                    Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, @NonNull Throwable error) {
                isInProcess=false;
                favori.setChecked(true);
                Toast.makeText(getBaseContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        kulup_adi.setText(intent.getStringExtra("kulup_adi"));
        baslik.setText(intent.getStringExtra("baslik"));
        aciklama.setText(intent.getStringExtra("aciklama"));
        tarih.setText(intent.getStringExtra("tarih"));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
