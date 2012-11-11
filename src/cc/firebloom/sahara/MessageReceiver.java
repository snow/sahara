package cc.firebloom.sahara;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsMessage;
import android.util.Log;
import cc.firebloom.sahara.keyword.KeywordFilter;
import cc.firebloom.sahara.sender.SenderFilter;

public class MessageReceiver extends BroadcastReceiver {
  private static final String TAG = "MessageReceiver";
  private static final String MMS_DATA_TYPE = "application/vnd.wap.mms-message";
  
  protected static ArrayList<String> contactNumbers;

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
      //boolean _isSpam = false;
      Object[] pdus = (Object[]) uinf.get("pdus");
      if(null != pdus){
        String from = null;
        Long timestamp = null;
        String matchedRule = null;
        StringBuffer msgBodyBuffer = new StringBuffer();
        
        for (Object bytes:pdus) {
          SmsMessage sms = SmsMessage.createFromPdu((byte[]) bytes);
          msgBodyBuffer.append(sms.getMessageBody());
          
          if (null == from) {
            from = sms.getOriginatingAddress();
          }
          
          if (null == timestamp) {
            timestamp = sms.getTimestampMillis();
          }
        }
        
        String text = msgBodyBuffer.toString();
        matchedRule = isSpam(from, text, context);  
        
        if(null != matchedRule){
          persistMessage(from, text, timestamp, matchedRule, context);       
          
          abortBroadcast();
          
          //Log.i("> w <", String.format("blocked: [%s] %s", from, text));
        } //else {
          //Log.i("> w <", String.format("passed: [%s] %s", from, text));
        //}
      }
    }
  } // onReceive
  
  protected static String isSpam(String from, String text, Context context) {
    if (null == contactNumbers) {
      loadContacts(context);
    }
    
    from = from.replaceAll("[ +_-]", "");
    String matchedRule = SenderFilter.isSpam(from, context);
    
    if(null == matchedRule) {
      for(String contactNumber:contactNumbers) {
        if (from.endsWith(contactNumber)) {
          return null;
        }
      }
    }
    
    if(null == matchedRule){
      matchedRule = KeywordFilter.isSpam(text, context);
    }
    
    return matchedRule; 
  } // isSpam
  
  protected static void loadContacts(Context context) {
    Uri contactDataUri = Phone.CONTENT_URI;
    
    Cursor c = context.getContentResolver().query(contactDataUri, 
        new String[] {Phone.NUMBER}, 
        Phone.MIMETYPE + " = ?", 
        new String[] { Phone.CONTENT_ITEM_TYPE }, null);
    
    contactNumbers = new ArrayList<String>();
    while (c.moveToNext()) {
      String contactNumber = c.getString(0).replaceAll("[ +_-]", "");
      contactNumbers.add(contactNumber);
    }
  }

  private void persistMessage(String from, String text, Long timestamp, 
                              String matchedRule, Context context) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(timestamp);
    DateFormat yamlFormat = new SimpleDateFormat(Sahara.Yaml.DateTimeFormat);
    
    Map<String, String> record = new HashMap<String, String>();
    record.put(Sahara.Message.FROM, from);
    record.put(Sahara.Message.SENT_AT, yamlFormat.format(calendar.getTime()));
    record.put(Sahara.Message.TEXT, text);
    record.put(Sahara.Message.MATCHED_RULE, matchedRule);
    
    Yaml yaml = new Yaml();
    String msgYml = yaml.dump(record);
    
    DateFormat filenameFormat = new SimpleDateFormat("yyyy-MM-dd_HHmm_ss_SSS");
    String filename = String.format("%s_%s.yml", 
                                    filenameFormat.format(calendar.getTime()), 
                                    from);
    
    File bakDir = null;
    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
        // We can read and write the media
      File extStorageDir = new File(Sahara.Message.STORE_PATH);
      if(extStorageDir.exists() || extStorageDir.mkdirs()) {
        bakDir = extStorageDir;
      } 
    }
    
    if(null == bakDir){
      bakDir = context.getDir(Sahara.Message.STORE_DIR, Context.MODE_PRIVATE);
    }
    
//    saveStringToPath(msgYml, bakDir, filename);
    try {
      FileOutputStream fos = new FileOutputStream(new File(bakDir, filename));
      fos.write(msgYml.getBytes());
      fos.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  } // persistMessage
  
}
