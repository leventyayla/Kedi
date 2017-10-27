package tez.levent.feyyaz.kedi.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
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

import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;
import tez.levent.feyyaz.kedi.R;
import tez.levent.feyyaz.kedi.activities.MainActivity;
import tez.levent.feyyaz.kedi.activities.SplashScreen;

import static tez.levent.feyyaz.kedi.activities.SplashScreen.URL;

/**
 * Created by Levent on 10.05.2017.
 */

public class EtkinlikDetay extends Fragment {
    View view;
    String kulup_url, kulup_adi, puan=null;
    int etkinlik_id;
    TextView kulup_isim,puan_metni;
    ImageView kulup_logo;
    RatingBar puanBar;
    SparkButton favori;
    LinearLayout layout_favori;
    ProgressBar progress;
    Boolean isInProcess=false;
    String KULUP_IMAGE_DIR = SplashScreen.URL + "kulup_logo/";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        kulup_url = getArguments().getString("kulup_url");
        kulup_adi = getArguments().getString("kulup_adi");
        if (getArguments().getString("puan")!=null)
            puan = getArguments().getString("puan");
        etkinlik_id = getArguments().getInt("etkinlik_id");

        view = inflater.inflate(R.layout.frag_etkinlik_detay, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        kulup_isim = (TextView)view.findViewById(R.id.kulup_ad);
        kulup_logo = (ImageView)view.findViewById(R.id.kulup_logo);
        puanBar = (RatingBar)view.findViewById(R.id.puan);
        puan_metni = (TextView) view.findViewById(R.id.puan_metin);
        favori = (SparkButton)view.findViewById(R.id.favori_spark);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        layout_favori = (LinearLayout) view.findViewById(R.id.layout_favori);
        getFavoriDurumu();
    }

    @Override
    public void onStart() {
        super.onStart();

        Glide.with(getActivity().getApplicationContext())
                .load(KULUP_IMAGE_DIR + kulup_url)
                .crossFade()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(kulup_logo);
        kulup_isim.setText(kulup_adi);

        if (puan==null){
            puan_metni.setText("Puan henüz oluşmamış");
            puanBar.setVisibility(View.GONE);
        }else {
            DecimalFormat df = new DecimalFormat("#.#");
            puan_metni.setText(df.format(Float.parseFloat(puan)));
            float deger = Float.parseFloat(puan)/5;
            puanBar.setRating(deger);
        }

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
        SharedPreferences sharedPref = getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String k_adi = sharedPref.getString("k_adi",null);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        params.put("etkinlik_id", etkinlik_id);
        params.put("ogr_no", k_adi);
        client.post(URL + "android/get_favori_info.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull byte[] responseBody) {
                progress.setVisibility(View.GONE);
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    if (json.getBoolean("durum")){
                        favori.setChecked(true);
                    }else {
                        favori.setChecked(false);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
                layout_favori.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, @NonNull Throwable error) {
                progress.setVisibility(View.GONE);
                layout_favori.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void favoriEkle(){
        isInProcess = true;
        SharedPreferences sharedPref = getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String k_adi = sharedPref.getString("k_adi",null);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        params.put("etkinlik_id", etkinlik_id);
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
                        Toast.makeText(getContext(),json.getString("mesaj"),Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    favori.setChecked(false);
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, @NonNull Throwable error) {
                isInProcess=false;
                favori.setChecked(false);
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void favoriCikar(){
        isInProcess = true;
        SharedPreferences sharedPref = getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String k_adi = sharedPref.getString("k_adi",null);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        params.put("etkinlik_id", etkinlik_id);
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
                        Toast.makeText(getContext(),json.getString("mesaj"),Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    favori.setChecked(true);
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, @NonNull Throwable error) {
                isInProcess=false;
                favori.setChecked(true);
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Glide.clear(kulup_logo);
    }

}
