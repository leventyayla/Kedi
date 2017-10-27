package tez.levent.feyyaz.kedi.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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
import tez.levent.feyyaz.kedi.activities.details.EtkinlikActivity;
import tez.levent.feyyaz.kedi.adapters.EndlessRecyclerOnScrollListener;
import tez.levent.feyyaz.kedi.adapters.FavoriRecyclerViewAdapter;
import tez.levent.feyyaz.kedi.models.Favori;

public class FavorilerActivity extends AppCompatActivity {
    SwipeRefreshLayout refresh;
    EndlessRecyclerOnScrollListener scrollListener;
    private FavoriRecyclerViewAdapter mAdapter;
    int current_loaded_index = 0;
    RecyclerView mRecyclerView;
    TextView bos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoriler);

        bos = (TextView)findViewById(R.id.bos);
        refresh = (SwipeRefreshLayout) findViewById(R.id.swiperefreshEtkinlik);
        refresh.setColorSchemeResources(R.color.colorPrimary);

        mRecyclerView = (RecyclerView) findViewById(R.id.etkinlikler);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        scrollListener = new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore() {
                if (!refresh.isRefreshing()){

                    getFavoriler();
                    refresh.setRefreshing(true);
                }
            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);

        mAdapter = new FavoriRecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);

        getir();
        refresh.setRefreshing(true);

        mAdapter.setOnItemClickListener(new FavoriRecyclerViewAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (!refresh.isRefreshing()){
                    if (mAdapter.getFavori(position).getType() == Favori.ETKINLIK){

                        Intent intent = new Intent(getBaseContext(), EtkinlikActivity.class);
                        intent.putExtra("id",mAdapter.getFavori(position).getEtkinlik_id());
                        intent.putExtra("baslik", mAdapter.getFavori(position).getEtkinlik_adi());
                        intent.putExtra("aciklama", mAdapter.getFavori(position).getEtkinlik_aciklama());
                        intent.putExtra("sure", mAdapter.getFavori(position).getEtkinlik_sure());
                        intent.putExtra("tarih", mAdapter.getFavori(position).getEtkinlik_tarih());
                        intent.putExtra("kulup_isim", mAdapter.getFavori(position).getEtkinlik_kulup_isim());
                        intent.putExtra("son_duzenleme", mAdapter.getFavori(position).getEtkinlik_son_duzenleme());
                        intent.putExtra("qr_str", mAdapter.getFavori(position).getEtkinlik_qr_str());
                        intent.putExtra("url", mAdapter.getFavori(position).getEtkinlik_url());
                        String puan = mAdapter.getFavori(position).getEtkinlik_puan();
                        if (puan != null)
                            intent.putExtra("puan",puan);
                        intent.putExtra("kulup_url", mAdapter.getFavori(position).getEtkinlik_kulup_url());

                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(getBaseContext(), DuyuruActivity.class);
                        intent.putExtra("id",mAdapter.getFavori(position).getDuyuru_id());
                        intent.putExtra("kulup_adi",mAdapter.getFavori(position).getDuyuru_kulup_isim());
                        intent.putExtra("baslik",mAdapter.getFavori(position).getDuyuru_adi());
                        intent.putExtra("aciklama",mAdapter.getFavori(position).getDuyuru_aciklama());
                        intent.putExtra("tarih",mAdapter.getFavori(position).getDuyuru_tarih());
                        startActivity(intent);
                    }
                }
            }
        });

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getir();
            }
        });
    }

    private void getir(){
        scrollListener.reset(6);
        mAdapter.clearList();
        current_loaded_index = 0;
        getFavoriler();
    }

    private void getFavoriler(){
        SharedPreferences sharedPref = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String k_adi = sharedPref.getString("k_adi",null);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        final RequestParams params = new RequestParams();
        params.put("current_index", current_loaded_index);
        params.put("ogr_no", k_adi);
        client.post(SplashScreen.URL + "android/get_favoriler.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull byte[] responseBody) {

                String data = new String(responseBody);
                if (!data.equals("0")){
                    try {
                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject temp = jsonArray.getJSONObject(i);

                            if (!temp.getString("etkinlik_id").equals("null")){
                                String puan = null;
                                if (!temp.getString("etkinlik_puan").equals("null")){
                                    puan = temp.getString("etkinlik_puan");
                                }
                                Favori favori=new Favori(temp.getInt("etkinlik_id"),
                                        temp.getString("etkinlik_adi"),
                                        temp.getString("etkinlik_aciklama").trim(),
                                        temp.getString("ekinlik_url"),
                                        temp.getString("etkinlik_baslangic_tarihi")+" "+temp.getString("etkinlik_baslangic_saati"),
                                        temp.getInt("etkinlik_sure"),
                                        temp.getString("etkinlik_kulup_isim"),
                                        temp.getString("etkinlik_kulup_url"),
                                        temp.getString("etkinlik_son_duzenleme"),
                                        temp.getString("etkinlik_qr_str"),
                                        puan,
                                        temp.getString("tarih"));
                                mAdapter.addItem(favori);

                            }else if (!temp.getString("duyuru_id").equals("null")){

                                Favori favori = new Favori(temp.getInt("duyuru_id"),
                                        temp.getString("duyuru_adi"),
                                        temp.getString("duyuru_aciklama").trim(),
                                        temp.getString("duyuru_tarih"),
                                        temp.getString("duyuru_kulup_isim"),
                                        temp.getString("duyuru_kulup_url"),
                                        temp.getString("tarih"));
                                mAdapter.addItem(favori);
                            }
                        }
                        current_loaded_index = mAdapter.getItemCount();

                    } catch (JSONException e) {
                        Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }finally {
                        refresh.setRefreshing(false);
                    }
                }else {
                    refresh.setRefreshing(false);
                    if (current_loaded_index == 0)
                        bos.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                refresh.setRefreshing(false);
                scrollListener.setIsError(true);
                Snackbar.make(mRecyclerView,"Favoriler yüklenemedi!",Snackbar.LENGTH_LONG).show();
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

}
