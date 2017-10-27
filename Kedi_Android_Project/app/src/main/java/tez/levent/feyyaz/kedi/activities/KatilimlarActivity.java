package tez.levent.feyyaz.kedi.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
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
import tez.levent.feyyaz.kedi.adapters.EndlessRecyclerOnScrollListener;
import tez.levent.feyyaz.kedi.adapters.EtkinlikRecyclerViewAdapter;
import tez.levent.feyyaz.kedi.models.Etkinlik;

public class KatilimlarActivity extends AppCompatActivity {
    SwipeRefreshLayout refresh;
    EndlessRecyclerOnScrollListener scrollListener;
    private EtkinlikRecyclerViewAdapter mAdapter;
    int current_loaded_index = 0;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_katilimlar);

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

                    getKatilimlar();
                    refresh.setRefreshing(true);
                }
            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);

        mAdapter = new EtkinlikRecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);

        getir();
        refresh.setRefreshing(true);

        mAdapter.setOnItemClickListener(new EtkinlikRecyclerViewAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (!refresh.isRefreshing()){
                    Intent intent = new Intent(getBaseContext(), EtkinlikActivity.class);
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
                getir();
            }
        });
    }

    private void getir(){
        scrollListener.reset(3);
        mAdapter.clearList();
        current_loaded_index = 0;
        getKatilimlar();
    }

    private void getKatilimlar(){
        SharedPreferences sharedPref = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String k_adi = sharedPref.getString("k_adi",null);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        final RequestParams params = new RequestParams();
        params.put("current_index", current_loaded_index);
        params.put("ogr_no", k_adi);
        client.post(SplashScreen.URL + "android/get_katilimlar.php", params, new AsyncHttpResponseHandler() {
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
                                tempJsonEtkinlik.getString("aciklama"),
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
                    Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }finally {
                    refresh.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                refresh.setRefreshing(false);
                scrollListener.setIsError(true);
                Snackbar.make(mRecyclerView,"Katılımlar yüklenemedi!",Snackbar.LENGTH_LONG).show();
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
