package cc.firebloom.sahara;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import cc.firebloom.sahara.filters.SenderFilter;

public class SMSInterceptor extends BroadcastReceiver {
  private static final String TAG = "SMSInterceptor";

  @Override
  public void onReceive(Context ctx, Intent intend) {
    Bundle inf = intend.getExtras();

    if (null != inf) {
      Object[] pdus = (Object[]) inf.get("pdus");

      for (int i = 0; i < pdus.length; i++) {
        SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[i]);

        // --- dev ---
        String strMsgBody = sms.getMessageBody().toString();
        String strMsgSrc = sms.getOriginatingAddress();

        String strMessage = "SMS from " + strMsgSrc + " : " + strMsgBody;

        Log.d(TAG, "--------------------");
        Log.d(TAG, strMessage);
        // --- dev ---

        if (isSpam(sms, ctx)) {
          Log.d(TAG, "blocked!!");
          abortBroadcast();
          persistMessage(sms, ctx);
        }
      }
    }
  }

  private boolean isSpam(SmsMessage sms, Context ctx) {
    // return KeyworldFilter.isSpam(sms);
    return SenderFilter.isSpam(sms, ctx);
  }

  private void persistMessage(SmsMessage sms, Context ctx) {
    String yamlSkeleton = "---\n" + 
                          "from: %s\n" + 
                          "sent_at: %s\n" + 
                          //"received_at: %s\n" + 
                          "text: %s\n";
    
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(sms.getTimestampMillis());
    DateFormat yamlFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
    DateFormat filenameFormat = new SimpleDateFormat("yyyy-MM-dd_HHmm_ss_SSS");
    
    System.out.println(String.format(yamlSkeleton,
                                     sms.getOriginatingAddress(), 
                                     yamlFormat.format(calendar.getTime()),
                                     sms.getDisplayMessageBody()));
    
    String filename = filenameFormat.format(calendar.getTime()) + "_" + 
                        sms.getOriginatingAddress();
    Log.d(TAG, filename);
  }
}
