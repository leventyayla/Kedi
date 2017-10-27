package tez.levent.feyyaz.kedi.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;
import tez.levent.feyyaz.kedi.R;
import tez.levent.feyyaz.kedi.tools.RoundImageTransform;

import static tez.levent.feyyaz.kedi.activities.SplashScreen.URL;

public class ProfileActivity extends AppCompatActivity {
    SharedPreferences sharedPref;

    ProgressBar pb;

    String k_adi;
    RelativeLayout cikis_yap;
    ImageView profile_pic;
    TextView ad_soyad;
    Boolean is_getData_runned = false;
    private static final int MY_INTENT_CLICK=302;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        pb = (ProgressBar)findViewById(R.id.progressBar);
        cikis_yap = (RelativeLayout)findViewById(R.id.cikis_yap);
        profile_pic = (ImageView) findViewById(R.id.profile_image);
        ad_soyad = (TextView)findViewById(R.id.ad_soyad);

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Fotoğraf Seçin"),MY_INTENT_CLICK);
            }
        });

        sharedPref = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        k_adi = sharedPref.getString("k_adi",null);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == MY_INTENT_CLICK)
            {
                if (null == data) return;

                Uri selectedImageUri = data.getData();
                Bitmap bitmap = null, finalBitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (bitmap.getWidth() >= bitmap.getHeight()){
                    finalBitmap = Bitmap.createBitmap(bitmap,
                            bitmap.getWidth()/2 - bitmap.getHeight()/2,
                            0,
                            bitmap.getHeight(),
                            bitmap.getHeight());

                }else{
                    finalBitmap = Bitmap.createBitmap(bitmap,
                            0,
                            bitmap.getHeight()/2 - bitmap.getWidth()/2,
                            bitmap.getWidth(),
                            bitmap.getWidth());
                }

                final RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(getResources(), finalBitmap);
                dr.setCircular(true);

                profile_pic.post(new Runnable() {
                    @Override
                    public void run() {
                        Animation myFadeInAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in);
                        profile_pic.setImageDrawable(dr);
                        profile_pic.startAnimation(myFadeInAnimation);
                        Toast.makeText(ProfileActivity.this,"Seçilen fotoğrafı yükleme kodları henüz yazılmadı!",Toast.LENGTH_LONG).show();
                    }
                });

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        cikis_yap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.clear();
                        editor.apply();
                        Glide.get(getApplicationContext()).clearDiskCache();
                    }
                }).start();
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (!is_getData_runned){
            if (sharedPref.getString("user_name",null)!=null){
                try{
                    if(sharedPref.getString("url",null).equals("null")){
                        profile_pic.setImageResource(R.drawable.logo);
                    }else{
                        getUserPP(URL + "kullanici_pp/" + sharedPref.getString("url",null),
                                sharedPref.getString("son_duzenleme",null));
                        getUserData(k_adi);
                        /*RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(getResources(), result);
                        dr.setCircular(true);
                        profile_pic.setImageDrawable(dr);*/
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                ad_soyad.setText(sharedPref.getString("user_name",null));

            }else if(k_adi!=null){

                getUserData(k_adi);
            }
            is_getData_runned=true;
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
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
                    ad_soyad.setText(isim);
                    if (user.getString("url").equals("null")){
                        profile_pic.setImageResource(R.drawable.logo);
                    }else{
                        getUserPP(URL + "kullanici_pp/" +user.getString("url"),user.getString("son_duzenleme"));
                        Animation myFadeInAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in);
                        pb.setVisibility(View.VISIBLE);
                        pb.startAnimation(myFadeInAnimation);
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

        Glide.with(getApplicationContext())
                .load(url)
                .signature(new StringSignature(son_duzenleme))
                .centerCrop()
                .transform(new RoundImageTransform(this))
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        profile_pic.setImageResource(R.drawable.logo);
                        pb.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        pb.setVisibility(View.GONE);
                        return false;
                    }
                })
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(profile_pic);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.clear(profile_pic);
    }

    public String BitMapToString(@NonNull Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
    @Nullable
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}
