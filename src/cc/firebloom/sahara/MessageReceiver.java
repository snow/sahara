package cc.firebloom.sahara;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.damazio.notifier.event.receivers.mms.EncodedStringValue;
import org.damazio.notifier.event.receivers.mms.PduHeaders;
import org.damazio.notifier.event.receivers.mms.PduParser;
import org.yaml.snakeyaml.Yaml;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsMessage;
import android.util.Log;
import cc.firebloom.sahara.filters.KeyworldFilter;
import cc.firebloom.sahara.filters.SenderFilter;

public class MessageReceiver extends BroadcastReceiver {
  private static final String TAG = "MessageReceiver";
  private static final String MSG_BAK_DIR = "msg_bak";
  private static final String EXT_STORAGE_DIR = "cc.firebloom.sahara";
  
  private static final String MMS_DATA_TYPE = "application/vnd.wap.mms-message";

  @Override
  public void onReceive(Context context, Intent intent) {
    Bundle uinf = intent.getExtras();
    
    //Log.d(TAG, "data: "+ intent.getData() + " | " + intent.getType());
    //Log.d(TAG, "bundle keys: " + uinf.keySet().toString());
    
    // ---
    // MMS
    // ---
    if(MMS_DATA_TYPE.equalsIgnoreCase(intent.getType())) {
      PduParser parser = new PduParser();
      PduHeaders headers = parser.parseHeaders(intent.getByteArrayExtra("data"));
      if (headers == null) {
        Log.e(TAG, "Couldn't parse headers for WAP PUSH.");
        return;
      }
      
      int messageType = headers.getMessageType();
      //Log.d(TAG, "WAP PUSH message type: 0x" + Integer.toHexString(messageType));

      // Check if it's a MMS notification
      if (messageType == PduHeaders.MESSAGE_TYPE_NOTIFICATION_IND) {
        String from = null;
        EncodedStringValue encodedFrom = headers.getFrom();
        if (encodedFrom != null) {
          from = encodedFrom.getString();
        }
        
        //Log.d(TAG, "mms from " + from);
        //Log.d(TAG, "text: ");
        
        if(null != from){
          String matchedRule = isSpam(from, "", context);
          
          if (null != matchedRule) {
            //Log.d(TAG, "filtered mms from " + from);
            
            // headers don't contain a `sent at` time
            Date date = new Date();
            persistMessage(from, "mms message notification", date.getTime(), 
                           matchedRule, context);
            
            abortBroadcast();
          }
        }
      } else {
        Log.d(TAG, "other type of mms: " + messageType);
      }
      
      
    // ---
    // SMS
    // ---
    } else {
      boolean _isSpam = false;
      Object[] pdus = (Object[]) uinf.get("pdus");
      if(null != pdus){
        SmsMessage[] messages = new SmsMessage[pdus.length];
        String matchedRule = null;
        
        for (int i = 0; i < pdus.length; i++) {
          SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[i]);
          messages[i] = sms;
          
          // --- dev ---
          /*String strMsgBody = sms.getMessageBody().toString();
          String strMsgSrc = sms.getOriginatingAddress();

          String strMessage = "SMS from " + strMsgSrc + " : " + strMsgBody;

          Log.d(TAG, "--------------------");
          Log.d(TAG, strMessage);*/
          // --- dev ---
          
          if(!_isSpam){
            matchedRule = isSpam(sms.getOriginatingAddress(), 
                                 sms.getMessageBody().toString(), 
                                 context);        
            if(null != matchedRule){
              // call abortBroadcast() after all pdus being processed
              _isSpam = true;
            }
          }
        }
        
        if(_isSpam){
          for(SmsMessage sms:messages){
            persistMessage(sms, matchedRule, context);
          }        
          
          abortBroadcast();
        }
      }
    }
  } // onReceive
  
  private String isSpam(String from, String text, Context context) {
    String matchedRule = SenderFilter.isSpam(from, context);
    if(null == matchedRule){
      matchedRule = KeyworldFilter.isSpam(text, context);
    }
    
    return matchedRule; 
  } // isSpam
  
  private void persistMessage(SmsMessage sms, String matchedRule, Context context) {
    persistMessage(sms.getOriginatingAddress(), 
                   sms.getDisplayMessageBody(), 
                   sms.getTimestampMillis(), 
                   matchedRule, 
                   context);
  }

  private void persistMessage(String from, String text, Long timestamp, String matchedRule, Context context) {
//    String yamlSkeleton = "---\n" + 
//                          "from: %s\n" + 
//                          "sent_at: %s\n" + 
//                          //"received_at: %s\n" + 
//                          "text: %s\n" +
//                          "matched rule: %s\n";
    
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(timestamp);
    DateFormat yamlFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z"); 
    
//    String msgYml = String.format(yamlSkeleton,
//                                  from, 
//                                  yamlFormat.format(calendar.getTime()),
//                                  text,
//                                  matchedRule);
    
    Map<String, String> record = new HashMap<String, String>();
    record.put("from", from);
    record.put("sent_at", yamlFormat.format(calendar.getTime()));
    record.put("text", text);
    record.put("matched_rule", matchedRule);
    
    Yaml yaml = new Yaml();
    String msgYml = yaml.dump(record);
    
    DateFormat filenameFormat = new SimpleDateFormat("yyyy-MM-dd_HHmm_ss_SSS");
    String filename = String.format("%s_%s.yml", 
                                    filenameFormat.format(calendar.getTime()), 
                                    from);
    
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
      bakDir = context.getDir(MSG_BAK_DIR, Context.MODE_PRIVATE);
    }
    
//    saveStringToPath(msgYml, bakDir, filename);
    try {
      FileOutputStream fos = new FileOutputStream(new File(bakDir, filename));
      fos.write(msgYml.getBytes());
      fos.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  } // persistMessage
  
//  private void saveStringToPath(String text, File dir, String name){
//    //Log.d(TAG, "saving to: " + dir.getPath());
//    try {
//      FileOutputStream fos = new FileOutputStream(new File(dir, name));
//      fos.write(text.getBytes());
//      fos.close();
//    } catch (FileNotFoundException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    } catch (IOException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
//  } // saveStringToPath
  
}
