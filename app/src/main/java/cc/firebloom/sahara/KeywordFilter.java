package cc.firebloom.sahara;

import android.content.Context;

/**
 * Created by snowhs on 20141220.
 */
public class KeywordFilter {
    public static String isSpam(String text, Context context) {
        return Keyword.getInst(context).match(text);
    }
}
