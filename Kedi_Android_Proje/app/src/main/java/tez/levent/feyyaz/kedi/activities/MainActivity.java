package tez.levent.feyyaz.kedi.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import tez.levent.feyyaz.kedi.R;
import tez.levent.feyyaz.kedi.adapters.ViewPagerAdapter;
import tez.levent.feyyaz.kedi.services.GetData;
import tez.levent.feyyaz.kedi.tools.RoundImageTransform;

import static tez.levent.feyyaz.kedi.activities.SplashScreen.URL;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        MaterialSearchView.OnQueryTextListener{
    protected SharedPreferences sharedPref;
    protected ViewPager viewPager;
    public static ViewPagerAdapter pagerAdapter;
    private DrawerLayout drawer;
    private ImageView profile_pic;
    private TextView k_isim;
    private Boolean is_getData_runned = false;
    private SwitchCompat sw;
    public static Boolean Takiptekiler_IsShow = false;
    MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Navigation Drawer Starts
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //create default navigation drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //handling navigation view item event
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);
        View header = LayoutInflater.from(this).inflate(R.layout.drawer_header,navigationView,false);
        profile_pic = (ImageView) header.findViewById(R.id.foto);
        k_isim = (TextView) header.findViewById(R.id.k_isim);
        navigationView.addHeaderView(header);
        //Navigation Drawer Ends

        //TabLayout and ViewPager starts
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager)findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3); //3 den fazla yükleme yapmayacak
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);
        //TabLayout and ViewPager ends

        MenuItem item = navigationView.getMenu().findItem(R.id.takiptekiler);
        sw = (SwitchCompat) item.getActionView().findViewById(R.id.switchTakiptekiler);

        sharedPref = getSharedPreferences("preferences", Context.MODE_PRIVATE);

        if (sharedPref.getBoolean("takip_durumu",false)){
            Takiptekiler_IsShow=true;
            sw.setChecked(true);
        }

        //Bildirim için servisi başlat
        Intent i = new Intent(this,GetData.class);
        startService(i);
    }

    Boolean is_drawer_start_opened = false;
    @Override
    protected void onStart() {
        super.onStart();

        if (sharedPref.getString("user_name",null)!=null){
            try{
                if(sharedPref.getString("url",null).equals("null")){
                    profile_pic.setImageResource(R.drawable.logo);
                }else{
                    getUserPP(URL + "kullanici_pp/" +sharedPref.getString("url",null),
                            sharedPref.getString("son_duzenleme",null));
                    if (!is_getData_runned){
                        getUserData(sharedPref.getString("k_adi",null));
                        is_getData_runned=true;
                    }

                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            k_isim.setText(sharedPref.getString("user_name",null));

        }else if(sharedPref.getString("k_adi",null) != null){

            getUserData(sharedPref.getString("k_adi",null));
        }

        if (!is_drawer_start_opened){
            drawer.openDrawer(GravityCompat.START);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawer.closeDrawers();
                }
            },1500);
            is_drawer_start_opened=true;
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.settings:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                    }
                },300); break; //Navigation Drawer'ın kapanması için 300 milisaniye beklenir

            case R.id.profile:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getBaseContext(), ProfileActivity.class));
                    }
                },300); break;
            case R.id.katildiklarim:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getBaseContext(), KatilimlarActivity.class));
                    }
                },300); break;
            case R.id.favoriler:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getBaseContext(), FavorilerActivity.class));
                    }
                },300); break;
            case R.id.takiptekiler:
                sw.toggle();
                Takiptekiler_IsShow = sw.isChecked();
                if (Takiptekiler_IsShow) {
                    pagerAdapter.getEtkinlikler().yenileTakiptekiEtkinlikler();
                    if (pagerAdapter.getDuyurular().isContentLoaded){
                        pagerAdapter.getDuyurular().yenileTakiptekiDuyurular();
                    }
                }else {
                    pagerAdapter.getEtkinlikler().yenileEtkinlikler();
                    if (pagerAdapter.getDuyurular().isContentLoaded){
                        pagerAdapter.getDuyurular().yenileDuyurular();
                    }
                }
                if (searchView.isSearchOpen()){
                    searchView.closeSearch();
                }
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void takipDurumuDegisti(){
        if (Takiptekiler_IsShow) {
            pagerAdapter.getEtkinlikler().yenileTakiptekiEtkinlikler();
            if (pagerAdapter.getDuyurular().isContentLoaded){
                pagerAdapter.getDuyurular().yenileTakiptekiDuyurular();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.arama);
        searchView = (MaterialSearchView) findViewById(R.id.arama);
        searchView.setMenuItem(item);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                FrameLayout fl = (FrameLayout) findViewById(R.id.toolbar_container);
                AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) fl.getLayoutParams();
                params.setScrollFlags(0);
            }

            @Override
            public void onSearchViewClosed() {
                FrameLayout fl = (FrameLayout) findViewById(R.id.toolbar_container);
                AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) fl.getLayoutParams();
                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                                    | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (searchView.isSearchOpen()){
            searchView.closeSearch();
        } else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPref = getSharedPreferences("preferences",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("takip_durumu", Takiptekiler_IsShow);
        editor.apply();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(sharedPref.getString("k_adi",null)==null)
            finish();
    }

    public void getUserData(String ogr_no){
        final AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        final RequestParams params = new RequestParams();
        params.put("ogr_no", ogr_no);
        client.post(URL + "android/get_user_detail.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, @NonNull byte[] responseBody) {
                try {
                    JSONArray jsonArray = new JSONArray(new String(responseBody));
                    JSONObject user = jsonArray.getJSONObject(0);
                    String isim = user.getString("isim")+ " "+ user.getString("soyisim");
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("user_name", isim);
                    editor.putString("url",user.getString("url"));
                    editor.putString("son_duzenleme",user.getString("son_duzenleme"));
                    editor.apply();
                    k_isim.setText(isim);
                    if (user.getString("url").equals("null")){
                        profile_pic.setImageResource(R.drawable.logo);
                    }else{
                        getUserPP(URL + "kullanici_pp/" + user.getString("url"),user.getString("son_duzenleme"));
                    }

                } catch (JSONException e) {
                    Toast.makeText(getBaseContext(),"Kayıtlı kullanıcı bulunamadı",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, @NonNull Throwable error) {
                Toast.makeText(getBaseContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getUserPP(String url, @NonNull String son_duzenleme){
        Glide.with(this)
                .load(url)
                .signature(new StringSignature(son_duzenleme))
                .centerCrop()
                .transform(new RoundImageTransform(this))
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        profile_pic.setImageResource(R.drawable.logo);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(profile_pic);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        int EtkinlkTabId = 1;
        if (viewPager.getCurrentItem() == EtkinlkTabId){
            pagerAdapter.getEtkinlikler().yenileArananEtkinlikler(query);
        }else {
            pagerAdapter.getDuyurular().yenileArananDuyurular(query);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}