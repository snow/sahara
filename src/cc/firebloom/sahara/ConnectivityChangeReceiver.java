package cc.firebloom.sahara;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Intent i = new Intent(context, UpdateService.class);
    PendingIntent operation = PendingIntent.getService(
        context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
    
    AlarmManager almgr = 
        (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    int type = AlarmManager.ELAPSED_REALTIME;
    long intervalMillis = AlarmManager.INTERVAL_HALF_DAY;
    long triggerAtMillis = SystemClock.elapsedRealtime() + intervalMillis;
    almgr.setInexactRepeating(type, triggerAtMillis, intervalMillis, operation);
    
    //Log.w("-w-", "scheduled");
  }

}
