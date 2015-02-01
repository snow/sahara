/**
 *
 */
package cc.firebloom.sahara;

import android.content.Context;

/**
 * @author snow@firebloom.cc
 */
public class SenderFilter {

    public static String isSpam(String from, Context context) {
//    if(from.startsWith("+86") && (14 == from.length())){
//      from = from.substring(3).trim();
//    }
//    if(from.startsWith("86") && (13 == from.length())){
//      from = from.substring(2).trim();
//    }
        if (Sender.getInst().shouldBlockNumber(from)) {
            return from;
        } else {
            return null;
        }
    }
}
