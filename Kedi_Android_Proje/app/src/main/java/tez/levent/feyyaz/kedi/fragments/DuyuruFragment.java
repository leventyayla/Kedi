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
import tez.levent.feyyaz.kedi.activities.details.DuyuruActivity;
import tez.levent.feyyaz.kedi.activities.SplashScreen;
import tez.levent.feyyaz.kedi.adapters.DuyuruRecyclerViewAdapter;
import tez.levent.feyyaz.kedi.adapters.EndlessRecyclerOnScrollListener;
import tez.levent.feyyaz.kedi.models.Duyuru;

import static tez.levent.feyyaz.kedi.activities.MainActivity.Takiptekiler_IsShow;

//Created by Levent on 5.12.2016.

public class DuyuruFragment extends Fragment {
    DuyuruRecyclerViewAdapter mAdapter;
    SwipeRefreshLayout refresh;
    EndlessRecyclerOnScrollListener scrollListener;
    View view;
    public boolean isContentLoaded = false;
    private String AramaMetni = null;
    int current_loaded_index = 0;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isContentLoaded){
            isContentLoaded=true;

            refresh = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshKulup);
            refresh.setColorSchemeResources(R.color.colorPrimary);

            RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.duyurular);
            mRecyclerView.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);

            scrollListener = new EndlessRecyclerOnScrollListener(mLayoutManager) {
                @Override
                public void onLoadMore() {
                    if (!refresh.isRefreshing()){

                        if (AramaMetni != null){
                            getArananDuyurular(AramaMetni);
                        }else {
                            if (Takiptekiler_IsShow){
                                getTakiptekiDuyurular();
                            }else {
                                getDuyurular();
                            }
                        }
                        refresh.setRefreshing(true);
                    }
                }
            };
            mRecyclerView.addOnScrollListener(scrollListener);

            mAdapter = new DuyuruRecyclerViewAdapter();
            mRecyclerView.setAdapter(mAdapter);

            if (Takiptekiler_IsShow){
                yenileTakiptekiDuyurular();
            }else {
                yenileDuyurular();
            }

            mAdapter.setOnItemClickListener(new DuyuruRecyclerViewAdapter.MyClickListener() {
                @Override
                public void onItemClick(int position, View v) {

                    if (!refresh.isRefreshing()){
                        Intent intent = new Intent(getContext(), DuyuruActivity.class);
                        intent.putExtra("id",mAdapter.getDuyuru(position).getId());
                        intent.putExtra("kulup_adi",mAdapter.getDuyuru(position).getKulup_adi());
                        intent.putExtra("baslik",mAdapter.getDuyuru(position).getBaslik());
                        intent.putExtra("aciklama",mAdapter.getDuyuru(position).getAciklama());
                        intent.putExtra("tarih",mAdapter.getDuyuru(position).getTarih());
                        startActivity(intent);
                    }
                }
            });
            refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (Takiptekiler_IsShow){
                        scrollListener.reset(6);
                        mAdapter.clearList();
                        current_loaded_index = 0;
                        AramaMetni = null;
                        getTakiptekiDuyurular();
                    }else {
                        scrollListener.reset(6);
                        mAdapter.clearList();
                        current_loaded_index = 0;
                        AramaMetni = null;
                        getDuyurular();
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        view = inflater.inflate(R.layout.fragment_duyuru, container, false);
        return view;
    }

    public void yenileDuyurular(){
        if (!refresh.isRefreshing()){
            scrollListener.reset(6);
            mAdapter.clearList();
            current_loaded_index = 0;
            AramaMetni = null;
            getDuyurular();
            refresh.setRefreshing(true);
        }else {
            Snackbar.make(view,"Devam eden işlem var! Tüm duyuruları getirmek için işlem sonunda çekip yenileyiniz",Snackbar.LENGTH_LONG).show();
        }
    }

    public void yenileTakiptekiDuyurular(){
        if (!refresh.isRefreshing()){
            scrollListener.reset(6);
            mAdapter.clearList();
            current_loaded_index = 0;
            AramaMetni = null;
            getTakiptekiDuyurular();
            refresh.setRefreshing(true);
        }else {
            Snackbar.make(view,"Devam eden işlem var! Duyuruları filtrelemek için işlem sonunda çekip yenileyiniz",Snackbar.LENGTH_LONG).show();
        }
    }

    public void yenileArananDuyurular(String metin){
        if (!refresh.isRefreshing()){
            scrollListener.reset(6);
            mAdapter.clearList();
            current_loaded_index = 0;
            AramaMetni = metin;
            getArananDuyurular(AramaMetni);
            refresh.setRefreshing(true);
        }else {
            Snackbar.make(view,"Devam eden işlem var! İşlem sonunda tekrar arama yapınız",Snackbar.LENGTH_LONG).show();
        }
    }

    private void getDuyurular(){

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        final RequestParams params = new RequestParams();
        params.put("current_index", current_loaded_index);
        client.post(SplashScreen.URL + "android/get_duyuru.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull byte[] responseBody) {

                try {
                    JSONArray jsonArray = new JSONArray(new String(responseBody));
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject tempJsonDuyuru = jsonArray.getJSONObject(i);
                        final Duyuru tempDuyuru=new Duyuru(tempJsonDuyuru.getInt("duyuru_id"),
                                tempJsonDuyuru.getString("baslik"),
                                tempJsonDuyuru.getString("aciklama").trim(),
                                tempJsonDuyuru.getString("tarih"),
                                tempJsonDuyuru.getString("kulup_isim"));
                        mAdapter.addItem(tempDuyuru);
                    }
                    current_loaded_index = mAdapter.getItemCount();

                } catch (JSONException e) {
                    Toast.makeText(getContext(),e.toString(),Toast.LENGTH_LONG).show();
                }finally {
                    refresh.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                refresh.setRefreshing(false);
                scrollListener.setIsError(true);
                Snackbar.make(view,"Duyurular yüklenemedi!",Snackbar.LENGTH_LONG).setAction("Hata",null).show();
            }
        });

    }

    private void getTakiptekiDuyurular(){
        SharedPreferences sharedPref = getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String k_adi = sharedPref.getString("k_adi",null);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        final RequestParams params = new RequestParams();
        params.put("current_index", current_loaded_index);
        params.put("ogr_no", k_adi);
        client.post(SplashScreen.URL + "android/get_takipteki_duyuru.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull byte[] responseBody) {

                try {
                    JSONArray jsonArray = new JSONArray(new String(responseBody));
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject tempJsonDuyuru = jsonArray.getJSONObject(i);
                        final Duyuru tempDuyuru=new Duyuru(tempJsonDuyuru.getInt("duyuru_id"),
                                tempJsonDuyuru.getString("baslik"),
                                tempJsonDuyuru.getString("aciklama").trim(),
                                tempJsonDuyuru.getString("tarih"),
                                tempJsonDuyuru.getString("kulup_isim"));
                        mAdapter.addItem(tempDuyuru);
                    }
                    current_loaded_index = mAdapter.getItemCount();

                } catch (JSONException e) {
                    Toast.makeText(getContext(),e.toString(),Toast.LENGTH_LONG).show();
                }finally {
                    refresh.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                refresh.setRefreshing(false);
                scrollListener.setIsError(true);
                Snackbar.make(view,"Duyurular yüklenemedi!",Snackbar.LENGTH_LONG).setAction("Hata",null).show();
            }
        });
    }

    private void getArananDuyurular(String metin){

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        final RequestParams params = new RequestParams();
        params.put("current_index", current_loaded_index);
        params.put("metin", metin);
        client.post(SplashScreen.URL + "android/find_duyuru.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull byte[] responseBody) {

                try {
                    JSONArray jsonArray = new JSONArray(new String(responseBody));
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject tempJsonDuyuru = jsonArray.getJSONObject(i);
                        final Duyuru tempDuyuru=new Duyuru(tempJsonDuyuru.getInt("duyuru_id"),
                                tempJsonDuyuru.getString("baslik"),
                                tempJsonDuyuru.getString("aciklama").trim(),
                                tempJsonDuyuru.getString("tarih"),
                                tempJsonDuyuru.getString("kulup_isim"));
                        mAdapter.addItem(tempDuyuru);
                    }
                    current_loaded_index = mAdapter.getItemCount();

                } catch (JSONException e) {
                    Toast.makeText(getContext(),e.toString(),Toast.LENGTH_LONG).show();
                }finally {
                    refresh.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                refresh.setRefreshing(false);
                scrollListener.setIsError(true);
                Snackbar.make(view,"Aranan duyurular yüklenemedi!",Snackbar.LENGTH_LONG).setAction("Hata",null).show();
            }
        });
    }
}