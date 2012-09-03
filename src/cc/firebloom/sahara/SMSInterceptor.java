package cc.firebloom.sahara;

import cc.firebloom.sahara.filters.KeyworldFilter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSInterceptor extends BroadcastReceiver {
	private static final String TAG = "SMSInterceptor";
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		Bundle inf = arg1.getExtras();
	    
	    if(null != inf){
	      Object[] smsextras = (Object[]) inf.get( "pdus" );

	            for ( int i = 0; i < smsextras.length; i++ )
	            {
	                SmsMessage sms = SmsMessage.createFromPdu((byte[])smsextras[i]);
	                
	                // --- dev ---
	                String strMsgBody = sms.getMessageBody().toString();
	                String strMsgSrc = sms.getOriginatingAddress();

	                String strMessage = "SMS from " + strMsgSrc + " : " + strMsgBody;                    

	                Log.d(TAG, strMessage);
	                // --- dev ---
	                
	                if(isSpam(sms)){
	                	abortBroadcast();
	                }
	            }
	    }
	}
	
	public boolean isSpam(SmsMessage sms){
		return KeyworldFilter.isSpam(sms);
	}
}
