package tez.levent.feyyaz.kedi.activities.details;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import tez.levent.feyyaz.kedi.R;
import tez.levent.feyyaz.kedi.activities.SplashScreen;
import tez.levent.feyyaz.kedi.fragments.EtkinlikDetay;
import tez.levent.feyyaz.kedi.fragments.EtkinlikHakkinda;
import tez.levent.feyyaz.kedi.fragments.EtkinlikYorumlar;

import static tez.levent.feyyaz.kedi.activities.SplashScreen.URL;

public class EtkinlikActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    final int REQUEST_CAMERA = 34;
    SharedPreferences sharedPref;
    BottomNavigationView navigation;
    private int id,sure;
    ImageView gorsel;
    private String tarih,kulup_isim,kulup_url,qr_str,url,son_duzenleme,baslik,aciklama,puan=null;
    private String ETKINLIK_IMAGE_DIR = SplashScreen.URL + "etkinlik_foto/";
    Boolean test = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etkinlik);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout ct = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        ct.setExpandedTitleColor(Color.parseColor("#00FFFFFF")); //Action yazısının geniş halde şeffaf olması

        sharedPref = getSharedPreferences("preferences", Context.MODE_PRIVATE);

        id = getIntent().getIntExtra("id",0);
        sure = getIntent().getIntExtra("sure",0);
        tarih = getIntent().getStringExtra("tarih");
        kulup_isim = getIntent().getStringExtra("kulup_isim");
        kulup_url = getIntent().getStringExtra("kulup_url");
        son_duzenleme = getIntent().getStringExtra("son_duzenleme");
        qr_str = getIntent().getStringExtra("qr_str");
        aciklama = getIntent().getStringExtra("aciklama");
        baslik = getIntent().getStringExtra("baslik");
        url = getIntent().getStringExtra("url");
        if (getIntent().getStringExtra("puan")!= null)
            puan = getIntent().getStringExtra("puan");

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        getSupportFragmentManager().popBackStack();
        Bundle hakkinda = new Bundle();
        hakkinda.putString("aciklama", aciklama);
        hakkinda.putString("baslik", baslik);
        EtkinlikHakkinda eh = new EtkinlikHakkinda();
        eh.setArguments(hakkinda);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, eh).commit();

        gorsel = (ImageView) findViewById(R.id.gorsel);

        Glide.with(getApplicationContext())
                .load(ETKINLIK_IMAGE_DIR + url)
                .signature(new StringSignature(son_duzenleme))
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .into(gorsel);

        gorsel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resim = new Intent(getBaseContext(),EtkinlikPhotoActivity.class);
                resim.putExtra("son_duzenleme",son_duzenleme);
                resim.putExtra("url",ETKINLIK_IMAGE_DIR + url);
                startActivity(resim);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View view) {
                kameraAc();
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

    public void kameraAc(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                Intent i = new Intent(getBaseContext(), EtkinlikQrReader.class);
                startActivityForResult(i, 1);
            }else {
                requestPermissions(new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
            }
        }else {
            Intent i = new Intent(getBaseContext(), EtkinlikQrReader.class);
            startActivityForResult(i, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_CAMERA){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                kameraAc();
            }else {
                Toast.makeText(this,"QR Kod okutabilmek için kamera iznini etkinleştirmeniz gerekli!",Toast.LENGTH_SHORT).show();
            }
        }else {
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String text = data.getStringExtra("QrData");
                if (qr_str.equals(text)){
                    katilimEkle();
                }else {
                    Toast.makeText(this,"Geçerli kod değil!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void katilimEkle(){

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        params.put("etkinlik_id", id);
        params.put("ogr_no", sharedPref.getString("k_adi",null));
        client.post(URL + "android/insert_katilim.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull byte[] responseBody) {

                try {

                    JSONObject json = new JSONObject(new String(responseBody));
                    Toast.makeText(getBaseContext(),json.getString("mesaj"),Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, @NonNull Throwable error) {
                Toast.makeText(getBaseContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.about:
                getSupportFragmentManager().popBackStack();

                Bundle hakkinda = new Bundle();
                hakkinda.putString("aciklama", aciklama);
                hakkinda.putString("baslik", baslik);
                EtkinlikHakkinda eh = new EtkinlikHakkinda();
                eh.setArguments(hakkinda);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, eh).commit();
                return true;

            case R.id.comments:
                getSupportFragmentManager().popBackStack();

                Bundle yorumlar = new Bundle();
                yorumlar.putInt("etkinlik_id", id);
                EtkinlikYorumlar ey = new EtkinlikYorumlar();
                ey.setArguments(yorumlar);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, ey).commit();
                /*      .addToBackStack(null) ile geri tuşuna basılınca
                        eklediğinin kaldırılmasına izin veriyor        */
                return true;

            case R.id.detay:
                getSupportFragmentManager().popBackStack();

                Bundle detay = new Bundle();
                detay.putString("kulup_url", kulup_url);
                detay.putString("kulup_adi", kulup_isim);
                detay.putInt("etkinlik_id", id);
                if (puan!=null)
                    detay.putString("puan", puan);
                EtkinlikDetay ed = new EtkinlikDetay();
                ed.setArguments(detay);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, ed).commit();
                /*      .addToBackStack(null) ile geri tuşuna basılınca
                        eklediğinin kaldırılmasına izin veriyor        */
                return true;
        }
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        id = intent.getIntExtra("id",0);
        sure = intent.getIntExtra("sure",0);
        tarih = intent.getStringExtra("tarih");
        kulup_isim = intent.getStringExtra("kulup_isim");
        son_duzenleme = intent.getStringExtra("son_duzenleme");
        qr_str = intent.getStringExtra("qr_str");
        aciklama = intent.getStringExtra("aciklama");
        baslik = intent.getStringExtra("baslik");
        url = intent.getStringExtra("url");

        getSupportFragmentManager().popBackStack();

        Bundle hakkinda = new Bundle();
        hakkinda.putString("aciklama", aciklama);
        hakkinda.putString("baslik", baslik);
        EtkinlikHakkinda eh = new EtkinlikHakkinda();
        eh.setArguments(hakkinda);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, eh).commit();

        navigation.setSelectedItemId(R.id.about);

        Glide.with(getApplicationContext())
                .load(ETKINLIK_IMAGE_DIR + url)
                .signature(new StringSignature(son_duzenleme))
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .into(gorsel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.clear(gorsel);
    }
}
