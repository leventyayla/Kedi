package tez.levent.feyyaz.kedi.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

public class LoginActivity extends AppCompatActivity {
    Button oturum_ac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Button kayit_ol = (Button) findViewById(R.id.kayit_ol);
        oturum_ac = (Button) findViewById(R.id.oturum_ac);

        kayit_ol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        oturum_ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oturum_ac.setEnabled(false);
                String k_adi="",k_sif_b="";
                try {
                    byte[] data = ((EditText)findViewById(R.id.sif)).getText().toString().getBytes("UTF-8");
                    k_sif_b = Base64.encodeToString(data, Base64.DEFAULT).trim();
                    k_adi =((EditText) findViewById(R.id.ogrenci_no)).getText().toString();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                OturumAc(k_adi,k_sif_b);
            }
        });

    }

    void OturumAc (@NonNull final String k_adi, @NonNull final String k_sif){
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory()); //Hosting SSL kullandığı için kütüphaneyi yapılandırıyoruz
        RequestParams params = new RequestParams();
        params.put("k_adi", k_adi);
        params.put("k_sif", k_sif);

        Boolean isEmpty = k_adi.isEmpty() || k_sif.isEmpty();
        if (!isEmpty){
            client.post(URL + "android/login.php", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, @NonNull byte[] responseBody) {
                    try {
                        JSONObject json = new JSONObject(new String(responseBody));
                        if (json.getBoolean("status")){
                            SharedPreferences sharedPref = getSharedPreferences("preferences",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("k_adi", k_adi);
                            editor.putString("k_sif", k_sif);
                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            oturum_ac.setEnabled(true);
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setMessage("Kullanıcı adı veya şifre hatalı!")
                                    .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                                    .show();
                        }
                    } catch (JSONException e) {
                        new AlertDialog.Builder(LoginActivity.this)
                                .setMessage(e.getMessage())
                                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .show();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, @NonNull Throwable error) {
                    oturum_ac.setEnabled(true);
                    new AlertDialog.Builder(LoginActivity.this)
                            .setMessage(error.getMessage())
                            .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .show();
                }
            });
        }else {
            oturum_ac.setEnabled(true);
            new AlertDialog.Builder(LoginActivity.this)
                    .setMessage("Lütfen eksiksiz doldurunuz!")
                    .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
        }
    }

}
