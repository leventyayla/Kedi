package tez.levent.feyyaz.kedi.activities.details;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
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
import tez.levent.feyyaz.kedi.activities.MainActivity;
import tez.levent.feyyaz.kedi.activities.SplashScreen;
import tez.levent.feyyaz.kedi.fragments.KulupFragment;

import static tez.levent.feyyaz.kedi.activities.SplashScreen.URL;

public class KulupActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    ImageView logo;
    int kulup_id,position;
    TextView adi, hakkimizda;
    Boolean isInProcess=false;
    SparkButton takip_spark;
    String KULUP_IMAGE_DIR = SplashScreen.URL + "kulup_logo/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kulup);

        takip_spark = (SparkButton) findViewById(R.id.takip_spark);
        logo=(ImageView)findViewById(R.id.kulup_logo);
        adi=(TextView)findViewById(R.id.kulup_adi);
        hakkimizda =(TextView)findViewById(R.id.hakkimizda);
        sharedPref = getSharedPreferences("preferences", Context.MODE_PRIVATE);

        adi.setText(getIntent().getStringExtra("isim"));
        hakkimizda.setText(getIntent().getStringExtra("hakkimizda"));
        kulup_id = getIntent().getIntExtra("kulup_id",0);
        position = getIntent().getIntExtra("position",0);

        takip_spark.setChecked(getIntent().getBooleanExtra("takip",false));

        takip_spark.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if (!isInProcess){
                    if (buttonState) {
                        takipEt();
                    } else {
                        takibiBirak();
                    }
                }
            }
        });

        if (getIntent().getStringExtra("url")!=null){
            Glide.with(getApplicationContext())
                    .load(KULUP_IMAGE_DIR + getIntent().getStringExtra("url"))
                    .signature(new StringSignature(getIntent().getStringExtra("son_duzenleme")))
                    .crossFade()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(logo);
        }else {
            logo.setImageResource(R.drawable.logo);
        }
    }

    private void takipEt(){
        isInProcess = true;

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        params.put("kulup_id", kulup_id);
        params.put("ogr_no", sharedPref.getString("k_adi",null));
        client.post(URL + "android/insert_takip.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull byte[] responseBody) {
                isInProcess=false;
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    if (json.getBoolean("durum")){
                        takip_spark.setChecked(true);
                        KulupFragment.mAdapter.getKulup(position).setFollowing(true);
                        KulupFragment.mAdapter.notifyItemChanged(position);
                        MainActivity.takipDurumuDegisti();
                    }else {
                        takip_spark.setChecked(false);
                        Toast.makeText(getBaseContext(),json.getString("mesaj"),Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    takip_spark.setChecked(false);
                    Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, @NonNull Throwable error) {
                isInProcess=false;
                takip_spark.setChecked(false);
                Toast.makeText(getBaseContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void takibiBirak(){
        isInProcess=true;

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        params.put("kulup_id", kulup_id);
        params.put("ogr_no", sharedPref.getString("k_adi",null));
        client.post(URL + "android/delete_takip.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull byte[] responseBody) {
                isInProcess=false;
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    if (json.getBoolean("durum")){
                        takip_spark.setChecked(false);
                        KulupFragment.mAdapter.getKulup(position).setFollowing(false);
                        KulupFragment.mAdapter.notifyItemChanged(position);
                        MainActivity.takipDurumuDegisti();
                    }else {
                        takip_spark.setChecked(true);
                        Toast.makeText(getBaseContext(),json.getString("mesaj"),Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    takip_spark.setChecked(true);
                    Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, @NonNull Throwable error) {
                takip_spark.setChecked(true);
                isInProcess=false;
                Toast.makeText(getBaseContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.clear(logo);
    }
}
