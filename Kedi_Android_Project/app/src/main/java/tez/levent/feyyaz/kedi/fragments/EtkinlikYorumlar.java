package tez.levent.feyyaz.kedi.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
import tez.levent.feyyaz.kedi.adapters.EtkinlikYorumAdapter;
import tez.levent.feyyaz.kedi.models.Yorum;
import tez.levent.feyyaz.kedi.tools.RoundImageTransform;

import static tez.levent.feyyaz.kedi.activities.SplashScreen.URL;

/**
 * Created by Levent on 8.03.2017.
 */

public class EtkinlikYorumlar extends Fragment {
    EtkinlikYorumAdapter mAdapter;
    SharedPreferences sharedPref;
    RatingBar puan;
    ProgressBar pb;
    ImageView pp;
    ImageButton silButon,gonderButon;
    EditText yorumMetni;
    TextView katilim;
    LinearLayout yorum;
    int etkinlikId,kullaniciYorumId;
    String ogr_no;
    Boolean isInProcess = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        etkinlikId = getArguments().getInt("etkinlik_id");
        return inflater.inflate(R.layout.frag_etkinlik_yorumlar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPref = getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        pb = (ProgressBar) view.findViewById(R.id.yuklenme);
        katilim = (TextView) view.findViewById(R.id.katilim);
        yorum = (LinearLayout) view.findViewById(R.id.yorum);
        yorumMetni = (EditText) view.findViewById(R.id.yorumMetni);
        silButon = (ImageButton) view.findViewById(R.id.delete);
        gonderButon = (ImageButton) view.findViewById(R.id.send);
        gonderButon.setTag("send");
        pp = (ImageView) view.findViewById(R.id.profile_image);
        puan = (RatingBar) view.findViewById(R.id.puan);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.yorumlar);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new EtkinlikYorumAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();

        ogr_no = sharedPref.getString("k_adi",null);

        gonderButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = gonderButon.getTag().toString();

                switch (tag){
                    case "update":
                        if (!yorumMetni.getText().toString().equals("")){
                            if (!isInProcess)
                            updateYorum(kullaniciYorumId,yorumMetni.getText().toString(),(int)puan.getRating());
                        }else{
                            Toast.makeText(getContext(),"Yorum boş bırakılamaz!",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "send":
                        if (!yorumMetni.getText().toString().equals("")){
                            if (!isInProcess)
                            sendYorum(etkinlikId,ogr_no,yorumMetni.getText().toString(),(int)puan.getRating());
                        }else{
                            Toast.makeText(getContext(),"Yorum boş bırakılamaz!",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });

        silButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isInProcess)
                deleteYorum(kullaniciYorumId);
            }
        });

        puan.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(@NonNull RatingBar ratingBar, float rating, boolean fromUser) {
                if(rating<1.0f)
                    ratingBar.setRating(1.0f);
            }
        });

        if(sharedPref.getString("url",null).equals("null")){
            pp.setImageResource(R.drawable.logo);
        }else{
            Glide.with(getActivity().getApplicationContext())
                    .load(URL + "kullanici_pp/" +sharedPref.getString("url",null))
                    .centerCrop()
                    .transform(new RoundImageTransform(getContext()))
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            pp.setImageResource(R.drawable.logo);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(pp);
        }

        getKullaniciYorum(ogr_no,etkinlikId);
        getYorumlar();

    }

    private void getKullaniciYorum(String ogr_no,int etkinlik_id){
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        final RequestParams params = new RequestParams();
        params.put("ogr_no", ogr_no);
        params.put("etkinlik_id", etkinlik_id);
        client.post(SplashScreen.URL + "android/get_user_yorum.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull final byte[] yorumData) {

                try {
                    JSONArray yorumArray = new JSONArray(new String(yorumData));
                    JSONObject JsonYorum = yorumArray.getJSONObject(0);

                    if (JsonYorum.getBoolean("katilim")){

                        yorum.setVisibility(View.VISIBLE);
                        if (JsonYorum.getBoolean("yorum_yapti_mi")){
                            kullaniciYorumId = JsonYorum.getInt("id");
                            yorumMetni.setEnabled(true);
                            yorumMetni.setText(JsonYorum.getString("icerik"));
                            puan.setRating(Float.parseFloat(JsonYorum.getString("puan")));

                            silButon.setVisibility(View.VISIBLE);

                            gonderButon.setImageResource(R.drawable.update_button);
                            gonderButon.setTag("update");

                        }else {
                            yorumMetni.setEnabled(true);
                        }
                    }else {
                        katilim.setVisibility(View.VISIBLE);
                    }

                }catch (Exception e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(),"Kullanıcı yorumu hakkında bilgi alınamadı!",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getYorumlar(){
        mAdapter.clearList();

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        final RequestParams params = new RequestParams();
        params.put("etkinlik_id", etkinlikId);
        client.post(SplashScreen.URL + "android/get_yorumlar.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull final byte[] yorumData) {
                try {
                    JSONArray yorumArray = new JSONArray(new String(yorumData));

                    for (int i = 0; i < yorumArray.length(); i++) {
                        JSONObject tempJsonYorum = yorumArray.getJSONObject(i);
                        Yorum tempYorum = new Yorum(tempJsonYorum.getInt("id"),
                                tempJsonYorum.getString("isim"),
                                tempJsonYorum.getString("soyisim"),
                                tempJsonYorum.getString("url"),
                                tempJsonYorum.getString("icerik"),
                                tempJsonYorum.getString("tarih"),
                                tempJsonYorum.getInt("puan"));
                        mAdapter.addItem(tempYorum);
                    }

                } catch (JSONException e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }finally {
                    pb.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pb.setVisibility(View.GONE);
                Toast.makeText(getContext(),"Yorumlar yüklenemedi!",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendYorum(int etkinlikId, String ogr_no, String yorum, int puan){
        isInProcess=true;

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        final RequestParams params = new RequestParams();
        params.put("etkinlik_id", etkinlikId);
        params.put("ogr_no", ogr_no);
        params.put("yorum", yorum);
        params.put("puan", puan);
        client.post(SplashScreen.URL + "android/insert_yorum.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull final byte[] yorumData) {
                isInProcess=false;

                try {
                    JSONObject json = new JSONObject(new String(yorumData));
                    if (json.getBoolean("durum")){
                        silButon.setVisibility(View.VISIBLE);

                        gonderButon.setImageResource(R.drawable.update_button);
                        gonderButon.setTag("update");
                        kullaniciYorumId=json.getInt("id");
                    }else {
                        Toast.makeText(getContext(),json.getString("mesaj"),Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }finally {
                    getYorumlar();
                    pb.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                isInProcess=false;
                Toast.makeText(getContext(),"Kullanıcı yorumu gönderilemedi!",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateYorum(int kullaniciYorumId, String icerik, int puan){
        isInProcess=true;

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        final RequestParams params = new RequestParams();
        params.put("yorum_id", kullaniciYorumId);
        params.put("icerik", icerik);
        params.put("puan", puan);
        client.post(SplashScreen.URL + "android/update_yorum.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull final byte[] yorumData) {
                isInProcess=false;

                try {
                    JSONObject json = new JSONObject(new String(yorumData));
                    if (!json.getBoolean("durum")){
                        Toast.makeText(getContext(),json.getString("mesaj"),Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }finally {
                    getYorumlar();
                    pb.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                isInProcess=false;
                Toast.makeText(getContext(),"Kullanıcı yorumu güncellenemedi!",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteYorum(int kullaniciYorumId){
        isInProcess=true;

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        final RequestParams params = new RequestParams();
        params.put("yorum_id", kullaniciYorumId);
        client.post(SplashScreen.URL + "android/delete_yorum.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull final byte[] yorumData) {
                isInProcess=false;

                try {
                    JSONObject json = new JSONObject(new String(yorumData));
                    if (json.getBoolean("durum")){
                        yorumMetni.setText("");

                        gonderButon.setImageResource(R.drawable.send_button);
                        gonderButon.setTag("send");

                        silButon.setVisibility(View.GONE);

                        puan.setRating(5);

                    }else {
                        Toast.makeText(getContext(),json.getString("mesaj"),Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }finally {
                    getYorumlar();
                    pb.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                isInProcess=false;
                Toast.makeText(getContext(),"Kullanıcı yorumu silinemedi!",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Glide.clear(pp);
    }
}
