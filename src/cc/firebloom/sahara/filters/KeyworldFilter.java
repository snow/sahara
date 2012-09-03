package cc.firebloom.sahara.filters;

import android.telephony.SmsMessage;

public class KeyworldFilter {
	
	static public boolean isSpam(SmsMessage sms){		
		String msgBody = sms.getMessageBody().toString();
		
		String[] keywordList = {"sb", "s2b"};
		
		for(String kw:keywordList){
			if(msgBody.contains(kw)){
				return true;
			}
		}
		
		return false;
	}
}
