package tez.levent.feyyaz.kedi.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import tez.levent.feyyaz.kedi.services.GetData;

public class AlarmStartService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent ıntent) {
        Intent i = new Intent(context,GetData.class);
        context.startService(i);

        Log.d("AlarmStartService","Çalıştı!");
    }
}