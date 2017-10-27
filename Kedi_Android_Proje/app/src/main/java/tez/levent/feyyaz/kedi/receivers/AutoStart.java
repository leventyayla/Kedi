package tez.levent.feyyaz.kedi.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class AutoStart extends BroadcastReceiver {

    final int REQUEST_CODE = 1;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            SharedPreferences sharedPref = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
            Boolean notification = sharedPref.getBoolean("bildirim",true);
            String k_adi = sharedPref.getString("k_adi",null);

            if (notification && (k_adi!=null)){
                Intent i = new Intent(context, AlarmStartService.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, i, 0);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        1000L,
                        AlarmManager.INTERVAL_HOUR, pendingIntent);
                Log.d("AutoStart","Servis alarmı kuruldu!");
            }else {
                Log.d("AutoStart","Servis kurulmadı! notification:" + String.valueOf(notification) + " k_adi:"+k_adi);
            }
        }
    }
}
