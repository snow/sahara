package cc.firebloom.sahara.filters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.firebloom.sahara.R;
import android.content.Context;
import android.content.res.Resources;
import android.telephony.SmsMessage;

public class KeyworldFilter {
  private static final String PLACEHOLDER_REQ_LINK = "__REQ_LN__";
  private static final String REGEX_LINK = "[\\d-]{5,13}|http://[\\w\\d.]+";
  private static final String REGEX_ZHCH_PUNCT = "[“！？；。，…【】《》『』]+";
	
	static public String isSpam(SmsMessage sms, Context ctx){
	  Resources res = ctx.getResources();
	  String[] keywords = res.getStringArray(R.array.init_keywords);
	  
		String msgBody = sms.getMessageBody().toString();
		msgBody = msgBody.replaceAll("\\s+", "").replaceAll("\\p{Punct}", "").
		            replaceAll(REGEX_ZHCH_PUNCT, "");
		
		for(String kw:keywords){
		  Pattern p = preprocessKeyword(kw);
		  Matcher m = p.matcher(msgBody);
			if(m.find()){
				return kw;
			}
		}
		
		return null;
	}
	
	static private Pattern preprocessKeyword(String keyword){
	  return Pattern.compile(keyword.replace(PLACEHOLDER_REQ_LINK, REGEX_LINK));
	}
}
