package tez.levent.feyyaz.kedi.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import tez.levent.feyyaz.kedi.R;

public class SplashScreen extends AppCompatActivity {
    public static final String URL = "https://leventyayla.com.tr/kedi/";
    SharedPreferences sharedPref;
    @Nullable
    String k_adi,k_sif;

    @Override
    protected void onStart() {
        super.onStart();

        sharedPref = getSharedPreferences("preferences",Context.MODE_PRIVATE);
        k_adi = sharedPref.getString("k_adi",null);
        k_sif = sharedPref.getString("k_sif",null);

        if (k_adi != null && k_sif != null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
                    RequestParams params = new RequestParams();
                    params.put("k_adi", k_adi);
                    params.put("k_sif", k_sif);
                    client.post(getBaseContext(),URL + "android/login.php", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, @NonNull byte[] responseBody) {
                            try {
                                JSONObject json = new JSONObject(new String(responseBody));
                                if (json.getBoolean("status")){
                                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.remove("user_name");
                                    editor.remove("user_profile");
                                    editor.apply();
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (JSONException e) {
                                new AlertDialog.Builder(SplashScreen.this)
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
                            new AlertDialog.Builder(SplashScreen.this)
                                    .setMessage(error.getMessage())
                                    .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                    });
                }
            }, 500);
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}