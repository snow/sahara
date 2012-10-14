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
  
  static public String isSpam(String from, Context context){   
    if(from.startsWith("+86")){
      from = from.substring(3).trim();
    }

    Resources res = context.getResources();
    String[] blocklist = res.getStringArray(R.array.init_blocklist);    
    for(String blockNum:blocklist){
      if(from.equalsIgnoreCase(blockNum)){
        return blockNum;
      }
    }
    
    return null;
  }
}
