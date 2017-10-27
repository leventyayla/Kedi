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
import tez.levent.feyyaz.kedi.activities.details.KulupActivity;
import tez.levent.feyyaz.kedi.activities.SplashScreen;
import tez.levent.feyyaz.kedi.adapters.KulupRecyclerViewAdapter;
import tez.levent.feyyaz.kedi.models.Kulup;

//Created by Levent on 5.12.2016.

public class KulupFragment extends Fragment {
    public static KulupRecyclerViewAdapter mAdapter;
    SwipeRefreshLayout refresh;
    View view;
    boolean isContentLoaded = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isContentLoaded){
            isContentLoaded=true;

            refresh = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshKulup);
            refresh.setColorSchemeResources(R.color.colorPrimary);
            RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.kulupler);
            mRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new KulupRecyclerViewAdapter();
            mRecyclerView.setAdapter(mAdapter);
            getKulupler();
            refresh.setRefreshing(true);

            mAdapter.setOnItemClickListener(new KulupRecyclerViewAdapter.MyClickListener() {
                @Override
                public void onItemClick(int position, View v) {

                    if (!refresh.isRefreshing()){
                        Intent intent = new Intent(getContext(), KulupActivity.class);
                        intent.putExtra("position",position);
                        intent.putExtra("kulup_id",mAdapter.getKulup(position).getId());
                        intent.putExtra("isim",mAdapter.getKulup(position).getIsim());
                        intent.putExtra("hakkimizda",mAdapter.getKulup(position).getHakkimizda());
                        intent.putExtra("takip",mAdapter.getKulup(position).getFollowing());
                        intent.putExtra("url",mAdapter.getKulup(position).getUrl());
                        intent.putExtra("son_duzenleme",mAdapter.getKulup(position).getSon_duzenleme());
                        startActivity(intent);
                    }
                }
            });
            refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getKulupler();
                }
            });
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        view = inflater.inflate(R.layout.fragment_kulup, container, false);
        return view;
    }

    private void getKulupler(){
        mAdapter.clearList();

        SharedPreferences sharedPref = getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String k_adi = sharedPref.getString("k_adi",null);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        final RequestParams params = new RequestParams();
        params.put("ogr_no", k_adi);
        client.post(SplashScreen.URL + "android/get_kulup.php",params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull final byte[] kulupData) {
                try {
                    JSONArray kulupArray = new JSONArray(new String(kulupData));

                    for (int i = 0; i < kulupArray.length(); i++) {
                        JSONObject tempJsonKulup = kulupArray.getJSONObject(i);
                        Kulup tempKulup = new Kulup(tempJsonKulup.getInt("kulup_id"),
                                tempJsonKulup.getString("isim"),
                                tempJsonKulup.getString("hakkimizda").trim(),
                                tempJsonKulup.getString("url"),
                                tempJsonKulup.getString("son_duzenleme"),
                                tempJsonKulup.getBoolean("takip_durumu"));
                        mAdapter.addItem(tempKulup);
                    }

                } catch (JSONException e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }finally {
                    refresh.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                refresh.setRefreshing(false);
                Snackbar.make(view,"Kulüpler yüklenemedi!",Snackbar.LENGTH_LONG).setAction("Hata",null).show();
            }
        });
    }
}
