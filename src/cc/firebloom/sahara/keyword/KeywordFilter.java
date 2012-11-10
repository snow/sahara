package cc.firebloom.sahara.keyword;

import android.content.Context;

public class KeywordFilter {
  
	static public String isSpam(String text, Context context){
	  return Keyword.getInst(context).match(text);
	}
}
