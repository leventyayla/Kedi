package tez.levent.feyyaz.kedi.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import tez.levent.feyyaz.kedi.R;

import static tez.levent.feyyaz.kedi.activities.SplashScreen.URL;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Button geri = (Button) findViewById(R.id.geri);
        geri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavUtils.navigateUpFromSameTask(SignUpActivity.this);
                finish();
            }
        });

        Button kayit_ol = (Button) findViewById(R.id.kayit_ol);
        kayit_ol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText ogr_no = (EditText) findViewById(R.id.ogrenci_no);
                EditText ad = (EditText) findViewById(R.id.ad);
                EditText soyad = (EditText) findViewById(R.id.soyad);
                EditText sif = (EditText) findViewById(R.id.sifre);
                String k_sif_b="";
                try {
                    byte[] data = sif.getText().toString().getBytes("UTF-8");
                    k_sif_b = Base64.encodeToString(data, Base64.DEFAULT).trim();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                KayitOl(ogr_no.getText().toString(),
                        ad.getText().toString(),
                        soyad.getText().toString(),
                        k_sif_b);
            }
        });
    }
    void KayitOl (@NonNull String ogr_no, @NonNull String ad, @NonNull String soyad, @NonNull String sif){
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        params.put("ogr_no", ogr_no);
        params.put("ad", ad);
        params.put("soyad", soyad);
        params.put("sif", sif);

        Boolean isEmpty = ogr_no.isEmpty() || ad.isEmpty() || soyad.isEmpty() || sif.isEmpty();
        if (!isEmpty){
            client.post(URL + "android/insert_user.php", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, @NonNull byte[] responseBody) {
                    try {
                        JSONObject json = new JSONObject(new String(responseBody));
                        if (json.getBoolean("status")){
                            new AlertDialog.Builder(SignUpActivity.this)
                                    .setMessage("Hesap oluşturuldu\nHesabınıza giriş yapabilirsiniz")
                                    .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    }).show();
                        }else {
                            new AlertDialog.Builder(SignUpActivity.this)
                                    .setMessage("Bu öğrenci numarası ile ilişkili bir hesap zaten mevcut!")
                                    .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                                    .show();
                        }
                    } catch (JSONException e) {
                        new AlertDialog.Builder(SignUpActivity.this)
                                .setMessage("Sunucuda bir hata oluştu!!")
                                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).show();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    new AlertDialog.Builder(SignUpActivity.this)
                            .setMessage("Çalışan bir internet bağlantısı gerekli!")
                            .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).show();
                }
            });
        }else {
            new AlertDialog.Builder(SignUpActivity.this)
                    .setMessage("Lütfen boş alan bırakmayınız!")
                    .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).show();
        }
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
        finish();
    }
}
