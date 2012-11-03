/**
 * 
 */
package cc.firebloom.sahara.sender;

import android.content.Context;
import android.util.Log;

/**
 * @author snow@firebloom.cc
 *
 */
public class SenderFilter {
  
  static public String isSpam(String from, Context context){   
    if(from.startsWith("+86") && (14 == from.length())){
      from = from.substring(3).trim();
    }
    if(from.startsWith("86") && (13 == from.length())){
      from = from.substring(2).trim();
    }
    
    Sender sender = new Sender(context);
    for(String blockNum:sender.fullList()){
      if(from.equalsIgnoreCase(blockNum)){
        return blockNum;
      }
    }
    
    return null;
  }
}
