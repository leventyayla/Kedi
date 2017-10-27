package tez.levent.feyyaz.kedi.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import tez.levent.feyyaz.kedi.R;
import tez.levent.feyyaz.kedi.activities.details.EtkinlikActivity;
import tez.levent.feyyaz.kedi.activities.SplashScreen;
import tez.levent.feyyaz.kedi.adapters.EndlessRecyclerOnScrollListener;
import tez.levent.feyyaz.kedi.adapters.EtkinlikRecyclerViewAdapter;
import tez.levent.feyyaz.kedi.models.Etkinlik;

import static tez.levent.feyyaz.kedi.activities.MainActivity.Takiptekiler_IsShow;

// Created by Levent on 5.12.2016. - Ali Emre BAL

public class EtkinlikFragment extends Fragment {
    private EtkinlikRecyclerViewAdapter mAdapter;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    EndlessRecyclerOnScrollListener scrollListener;
    View view;
    SwipeRefreshLayout refresh;
    private String AramaMetni = null;
    int current_loaded_index = 0;

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        view = inflater.inflate(R.layout.fragment_etkinlik, container, false);

        refresh = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshEtkinlik);
        refresh.setColorSchemeResources(R.color.colorPrimary);

        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mAdapter = new EtkinlikRecyclerViewAdapter();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.etkinlikler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        scrollListener = new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore() {
                if (!refresh.isRefreshing()){

                    if (AramaMetni != null){
                        getArananEtkinlikler(AramaMetni);
                    }else {
                        if (Takiptekiler_IsShow){
                            getTakiptekiEtkinlikler();
                        }else {
                            getEtkinlikler();
                        }
                    }
                    refresh.setRefreshing(true);
                }
            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);

        if (Takiptekiler_IsShow){
            yenileTakiptekiEtkinlikler();
        }else {
            yenileEtkinlikler();
        }

        mAdapter.setOnItemClickListener(new EtkinlikRecyclerViewAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {

                if (!refresh.isRefreshing()){
                    Intent intent = new Intent(getContext(), EtkinlikActivity.class);
                    intent.putExtra("id",mAdapter.getEtkinlik(position).getId());
                    intent.putExtra("baslik", mAdapter.getEtkinlik(position).getBaslik());
                    intent.putExtra("aciklama", mAdapter.getEtkinlik(position).getAciklama());
                    intent.putExtra("sure", mAdapter.getEtkinlik(position).getSure());
                    intent.putExtra("tarih", mAdapter.getEtkinlik(position).getTarih());
                    intent.putExtra("kulup_isim", mAdapter.getEtkinlik(position).getKulup_isim());
                    intent.putExtra("son_duzenleme", mAdapter.getEtkinlik(position).getSon_duzenleme());
                    intent.putExtra("qr_str", mAdapter.getEtkinlik(position).getQR_str());
                    intent.putExtra("url", mAdapter.getEtkinlik(position).getURL());
                    String puan = mAdapter.getEtkinlik(position).getPuan();
                    if (puan != null)
                        intent.putExtra("puan",puan);
                    intent.putExtra("kulup_url", mAdapter.getEtkinlik(position).getKulup_url());

                    startActivity(intent);
                }
            }
        });
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Takiptekiler_IsShow){
                    scrollListener.reset(3);
                    mAdapter.clearList();
                    current_loaded_index = 0;
                    AramaMetni = null;
                    getTakiptekiEtkinlikler();
                }else {
                    scrollListener.reset(3);
                    mAdapter.clearList();
                    current_loaded_index = 0;
                    AramaMetni = null;
                    getEtkinlikler();
                }
            }
        });

        return view;
    }

    public void yenileEtkinlikler(){
        if (!refresh.isRefreshing()){
            scrollListener.reset(3);
            mAdapter.clearList();
            current_loaded_index = 0;
            AramaMetni = null;
            getEtkinlikler();
            refresh.setRefreshing(true);
        }else {
            Snackbar.make(view,"Devam eden işlem var! Tüm etkinlikleri getirmek için işlem sonunda çekip yenileyiniz",Snackbar.LENGTH_LONG).show();
        }
    }

    public void yenileTakiptekiEtkinlikler(){
        if (!refresh.isRefreshing()){
            scrollListener.reset(3);
            mAdapter.clearList();
            current_loaded_index = 0;
            AramaMetni = null;
            getTakiptekiEtkinlikler();
            refresh.setRefreshing(true);
        }else {
            Snackbar.make(view,"Devam eden işlem var! Etkinlikleri filtrelemek için işlem sonunda çekip yenileyiniz",Snackbar.LENGTH_LONG).show();
        }
    }

    public void yenileArananEtkinlikler(String metin){
        if (!refresh.isRefreshing()){
            scrollListener.reset(6);
            mAdapter.clearList();
            current_loaded_index = 0;
            AramaMetni = metin;
            getArananEtkinlikler(AramaMetni);
            refresh.setRefreshing(true);
        }else {
            Snackbar.make(view,"Devam eden işlem var! İşlem sonunda tekrar arama yapınız",Snackbar.LENGTH_LONG).show();
        }
    }

    private void getEtkinlikler(){

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        final RequestParams params = new RequestParams();
        params.put("current_index", current_loaded_index);
        client.post(SplashScreen.URL + "android/get_etkinlik.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull byte[] responseBody) {

                try {
                    JSONArray jsonArray = new JSONArray(new String(responseBody));
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject tempJsonEtkinlik = jsonArray.getJSONObject(i);
                        String puan = null;
                        if (!tempJsonEtkinlik.getString("puan").equals("null")){
                            puan = tempJsonEtkinlik.getString("puan");
                        }
                        Etkinlik tempEtkinlik=new Etkinlik(tempJsonEtkinlik.getInt("etkinlik_id"),
                                tempJsonEtkinlik.getString("baslik"),
                                tempJsonEtkinlik.getString("aciklama").trim(),
                                tempJsonEtkinlik.getString("url"),
                                tempJsonEtkinlik.getInt("sure"),
                                tempJsonEtkinlik.getString("baslangic_tarihi")+" "+tempJsonEtkinlik.getString("baslangic_saati"),
                                tempJsonEtkinlik.getString("son_duzenleme"),
                                tempJsonEtkinlik.getString("kulup_isim"),
                                tempJsonEtkinlik.getString("qr_str"),
                                tempJsonEtkinlik.getString("kulup_url"),
                                puan);
                        mAdapter.addItem(tempEtkinlik);
                    }
                    current_loaded_index = mAdapter.getItemCount();

                } catch (JSONException e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }finally {
                    refresh.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                refresh.setRefreshing(false);
                scrollListener.setIsError(true);
                Snackbar.make(view,"Etkinlikler yüklenemedi!",Snackbar.LENGTH_LONG).setAction("Hata",null).show();
            }
        });
    }

    private void getTakiptekiEtkinlikler(){
        SharedPreferences sharedPref = getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String k_adi = sharedPref.getString("k_adi",null);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        final RequestParams params = new RequestParams();
        params.put("current_index", current_loaded_index);
        params.put("ogr_no", k_adi);
        client.post(SplashScreen.URL + "android/get_takipteki_etkinlik.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull byte[] responseBody) {

                try {
                    JSONArray jsonArray = new JSONArray(new String(responseBody));
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject tempJsonEtkinlik = jsonArray.getJSONObject(i);
                        String puan = null;
                        if (!tempJsonEtkinlik.getString("puan").equals("null")){
                            puan = tempJsonEtkinlik.getString("puan");
                        }
                        Etkinlik tempEtkinlik=new Etkinlik(tempJsonEtkinlik.getInt("etkinlik_id"),
                                tempJsonEtkinlik.getString("baslik"),
                                tempJsonEtkinlik.getString("aciklama").trim(),
                                tempJsonEtkinlik.getString("url"),
                                tempJsonEtkinlik.getInt("sure"),
                                tempJsonEtkinlik.getString("baslangic_tarihi")+" "+tempJsonEtkinlik.getString("baslangic_saati"),
                                tempJsonEtkinlik.getString("son_duzenleme"),
                                tempJsonEtkinlik.getString("kulup_isim"),
                                tempJsonEtkinlik.getString("qr_str"),
                                tempJsonEtkinlik.getString("kulup_url"),
                                puan);
                        mAdapter.addItem(tempEtkinlik);
                    }
                    current_loaded_index = mAdapter.getItemCount();

                } catch (JSONException e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }finally {
                    refresh.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                refresh.setRefreshing(false);
                scrollListener.setIsError(true);
                Snackbar.make(view,"Etkinlikler yüklenemedi!",Snackbar.LENGTH_LONG).setAction("Hata",null).show();
            }
        });
    }

    private void getArananEtkinlikler(String metin){

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        final RequestParams params = new RequestParams();
        params.put("current_index", current_loaded_index);
        params.put("metin", metin);
        client.post(SplashScreen.URL + "android/find_etkinlik.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull byte[] responseBody) {

                try {
                    JSONArray jsonArray = new JSONArray(new String(responseBody));
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject tempJsonEtkinlik = jsonArray.getJSONObject(i);
                        String puan = null;
                        if (!tempJsonEtkinlik.getString("puan").equals("null")){
                            puan = tempJsonEtkinlik.getString("puan");
                        }
                        Etkinlik tempEtkinlik=new Etkinlik(tempJsonEtkinlik.getInt("etkinlik_id"),
                                tempJsonEtkinlik.getString("baslik"),
                                tempJsonEtkinlik.getString("aciklama").trim(),
                                tempJsonEtkinlik.getString("url"),
                                tempJsonEtkinlik.getInt("sure"),
                                tempJsonEtkinlik.getString("baslangic_tarihi")+" "+tempJsonEtkinlik.getString("baslangic_saati"),
                                tempJsonEtkinlik.getString("son_duzenleme"),
                                tempJsonEtkinlik.getString("kulup_isim"),
                                tempJsonEtkinlik.getString("qr_str"),
                                tempJsonEtkinlik.getString("kulup_url"),
                                puan);
                        mAdapter.addItem(tempEtkinlik);
                    }
                    current_loaded_index = mAdapter.getItemCount();

                } catch (JSONException e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }finally {
                    refresh.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                refresh.setRefreshing(false);
                scrollListener.setIsError(true);
                Snackbar.make(view,"Aranan etkinlikler yüklenemedi!",Snackbar.LENGTH_LONG).setAction("Hata",null).show();
            }
        });
    }
}