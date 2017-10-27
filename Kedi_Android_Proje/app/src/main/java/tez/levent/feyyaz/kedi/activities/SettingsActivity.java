package tez.levent.feyyaz.kedi.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.DecimalFormat;

import tez.levent.feyyaz.kedi.R;
import tez.levent.feyyaz.kedi.receivers.AlarmStartService;

public class SettingsActivity extends AppCompatActivity {
    final int REQUEST_CODE = 1;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    SwitchCompat bildirim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeCache();

        Intent intent = new Intent(this, AlarmStartService.class);
        pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, 0);
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        bildirim = (SwitchCompat) findViewById(R.id.notify);
        SharedPreferences sharedPref = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        Boolean notification = sharedPref.getBoolean("bildirim",true);
        if (notification) bildirim.setChecked(true);

        bildirim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPref = getSharedPreferences("preferences",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("bildirim", isChecked);
                editor.apply();

                if (isChecked){
                    setAlarm();
                }else {
                    stopAlarm();
                }
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

    public void temizle(View v){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(getApplicationContext()).clearDiskCache();
            }
        }).start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initializeCache();
            }
        },500);
    }

    public void stopAlarm(){
        alarmManager.cancel(pendingIntent);
        Snackbar.make(bildirim,"Ayar kapatıldı",Snackbar.LENGTH_SHORT).show();
    }

    public void setAlarm(){
        alarmManager.cancel(pendingIntent);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                1000L,
                AlarmManager.INTERVAL_HOUR, pendingIntent);
        Snackbar.make(bildirim,"Ayar etkinleştirildi",Snackbar.LENGTH_SHORT).show();
    }

    private void initializeCache() {
        long size = 0;
        size += getDirSize(this.getCacheDir());
        size += getDirSize(this.getExternalCacheDir());
        ((TextView) findViewById(R.id.size)).setText(readableFileSize(size));
    }

    public long getDirSize(File dir){
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    public String readableFileSize(long size) {
        if (size <= 0) return "0 Bayt";
        final String[] units = new String[]{"Bayt", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
