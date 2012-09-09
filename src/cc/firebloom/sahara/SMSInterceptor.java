package cc.firebloom.sahara;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsMessage;
import cc.firebloom.sahara.filters.KeyworldFilter;
import cc.firebloom.sahara.filters.SenderFilter;

public class SMSInterceptor extends BroadcastReceiver {
  private static final String TAG = "SMSInterceptor";
  private static final String MSG_BAK_DIR = "msg_bak";
  private static final String EXT_STORAGE_DIR = "cc.firebloom.sahara";

  @Override
  public void onReceive(Context ctx, Intent intend) {
    Bundle inf = intend.getExtras();

    if (null != inf) {
      Object[] pdus = (Object[]) inf.get("pdus");

      for (int i = 0; i < pdus.length; i++) {
        SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[i]);

        // --- dev ---
        /*String strMsgBody = sms.getMessageBody().toString();
        String strMsgSrc = sms.getOriginatingAddress();

        String strMessage = "SMS from " + strMsgSrc + " : " + strMsgBody;

        Log.d(TAG, "--------------------");
        Log.d(TAG, strMessage);*/
        // --- dev ---

        String matchedRule =isSpam(sms, ctx);
        if(null != matchedRule){
          //Log.d(TAG, "blocked!!");
          abortBroadcast();
          persistMessage(sms, ctx, matchedRule);
        }
      }
    }
  }

  private String isSpam(SmsMessage sms, Context ctx) {
    // return KeyworldFilter.isSpam(sms);
    String matchedRule = SenderFilter.isSpam(sms, ctx);
    if(null == matchedRule){
      matchedRule = KeyworldFilter.isSpam(sms, ctx);
    }
    
    return matchedRule; 
  }

  private void persistMessage(SmsMessage sms, Context ctx, String matchedRule) {
    String yamlSkeleton = "---\n" + 
                          "from: %s\n" + 
                          "sent_at: %s\n" + 
                          //"received_at: %s\n" + 
                          "text: %s\n" +
                          "matched rule: %s\n";
    
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(sms.getTimestampMillis());
    DateFormat yamlFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");    
    
    String msgYml = String.format(yamlSkeleton,
                                  sms.getOriginatingAddress(), 
                                  yamlFormat.format(calendar.getTime()),
                                  sms.getDisplayMessageBody(),
                                  matchedRule);
    
    DateFormat filenameFormat = new SimpleDateFormat("yyyy-MM-dd_HHmm_ss_SSS");
    String filename = filenameFormat.format(calendar.getTime()) + "_" + 
                        sms.getOriginatingAddress() + ".yml";
    
    File bakDir = null;
    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
        // We can read and write the media
      File extStorageDir = new File(Environment.getExternalStorageDirectory().getPath() + 
                                      "/" + EXT_STORAGE_DIR);
      if(extStorageDir.exists() || extStorageDir.mkdir()) {
        File extBakDir = new File(extStorageDir.getPath() + "/" + MSG_BAK_DIR);
        if(extBakDir.exists() || extBakDir.mkdir()){
          bakDir = extBakDir;
        }
      } 
    } 
    
    if(null == bakDir){
      bakDir = ctx.getDir(MSG_BAK_DIR, Context.MODE_PRIVATE);
    }
    
    saveStringToPath(msgYml, bakDir, filename);
  }
  
  private void saveStringToPath(String text, File dir, String name){
    //Log.d(TAG, "saving to: " + dir.getPath());
    try {
      FileOutputStream fos = new FileOutputStream(new File(dir, name));
      fos.write(text.getBytes());
      fos.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
