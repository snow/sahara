/**
 * 
 */
package cc.firebloom.sahara.filters;

import android.content.Context;
import android.content.res.Resources;
import android.telephony.SmsMessage;
import cc.firebloom.sahara.R;

/**
 * @author snow@firebloom.cc
 *
 */
public class SenderFilter {
  private static final String TAG = "SenderFilter";
  
  static public String isSpam(SmsMessage sms, Context ctx){   
    String senderNumber = sms.getOriginatingAddress();
    if(senderNumber.startsWith("+86")){
      senderNumber = senderNumber.substring(3).trim();
    }

    Resources res = ctx.getResources();
    String[] blocklist = res.getStringArray(R.array.init_blocklist);    
    for(String blockNum:blocklist){
      if(senderNumber.equalsIgnoreCase(blockNum)){
        return blockNum;
      }
    }
    
    return null;
  }
}
