package cc.firebloom.sahara.filters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.firebloom.sahara.R;
import android.content.Context;
import android.content.res.Resources;
import android.telephony.SmsMessage;

public class KeyworldFilter {
	
	static public boolean isSpam(SmsMessage sms, Context ctx){		
	  Resources res = ctx.getResources();
	  String[] keywords = res.getStringArray(R.array.init_keywords);
	  
		String msgBody = sms.getMessageBody().toString();
		for(String kw:keywords){
		  Pattern p = Pattern.compile(kw);
		  Matcher m = p.matcher(msgBody);
			if(m.find()){
				return true;
			}
		}
		
		return false;
	}
}
