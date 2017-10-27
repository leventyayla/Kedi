package tez.levent.feyyaz.kedi.activities.details;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.github.chrisbanes.photoview.PhotoView;

import tez.levent.feyyaz.kedi.R;

public class EtkinlikPhotoActivity extends AppCompatActivity {
    PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etkinlik_photo);

        photoView = (PhotoView) findViewById(R.id.photo_view);

        Glide.with(getApplicationContext())
                .load(getIntent().getStringExtra("url"))
                .signature(new StringSignature(getIntent().getStringExtra("son_duzenleme")))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .into(photoView);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.clear(photoView);
    }
}
