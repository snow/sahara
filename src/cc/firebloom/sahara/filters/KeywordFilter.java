package cc.firebloom.sahara.filters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.Yaml;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import cc.firebloom.sahara.R;

public class KeywordFilter {
  private static final String PLACEHOLDER_REQ_LINK = "__REQ_LN__";
  private static final String REGEX_LINK = "[\\d-]{5,13}|http://[\\w\\d.]+";
  private static final String REGEX_ZHCN_PUNCT = "[“！？；。，…【】《》『』]+";
  private static final String KEYWORD_URI = "https://raw.github.com/snow/sahara/master/res/raw/init_keywords.yml";
	
	static public String isSpam(String text, Context context){
	  text = text.replaceAll("\\s+", "").replaceAll("\\p{Punct}", "").
		            replaceAll(REGEX_ZHCN_PUNCT, "");
		
		for(String kw:getKeywords(context)){
		  Pattern p = preprocessKeyword(kw);
		  Matcher m = p.matcher(text);
			if(m.find()){
				return kw;
			}
		}
		
		return null;
	}
	
	static public ArrayList<String> getKeywords(Context context) {
	  ArrayList<String> keywords = new ArrayList<String>();
	  Yaml yaml = new Yaml();
	  
	  try {
      ensureCache(context);
      
      InputStream cache = new FileInputStream(getCachePath(context));
      keywords = (ArrayList<String>)yaml.load(cache);
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    
    return keywords;
	}
	
	static public void ensureCache(Context context) throws IOException {
	  File cache = new File(getCachePath(context));
	  if(!cache.exists()) {
	    // only use built-in keywords here
	    // update online keywords later
	    // because network operations on main thread are forbidden
	    // and this method is called under sync context
	    exportInitKeywords(context);
	  }
	}
	
	static public void exportInitKeywords(Context context) throws IOException {
	  InputStream localKeywords = context.getResources().
        openRawResource(R.raw.init_keywords);
    FileOutputStream fos = new FileOutputStream(getCachePath(context));
    
    byte[] buffer = new byte[1024];
    int read;
    while((read = localKeywords.read(buffer)) != -1){
      fos.write(buffer, 0, read);
    }
    
    fos.flush();
    fos.close();
	}
	
	static public void updateKeywords(Context context) throws IOException {
	  ConnectivityManager connMgr = (ConnectivityManager) 
        context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    if (null == networkInfo || !networkInfo.isConnected()) {
       throw new RuntimeException("no network connection");
    }
    
    try {
      URL url = new URL(KEYWORD_URI);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setReadTimeout(10000 /* milliseconds */);
      conn.setConnectTimeout(5000 /* milliseconds */);
      conn.setRequestMethod("GET");
      conn.setDoInput(true);
      conn.connect();
      
      int respCode = conn.getResponseCode();
      if (200 == respCode) {
        InputStream is = conn.getInputStream();
        FileOutputStream fos = new FileOutputStream(getCachePath(context));

        byte[] buffer = new byte[1024];
        int read;
        while((read = is.read(buffer)) != -1){
          fos.write(buffer, 0, read);
        }
        
        fos.flush();
        fos.close();
      } else {
        Log.i("FIXME", String.format("got status code %s while downloading " +
        		                         "online keywords", respCode));
      }
      
    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
	}
	
	static public String getCachePath(Context context) {
	  return context.getCacheDir().getPath() + "/keywords.yml";
	}
	
	static private Pattern preprocessKeyword(String keyword){
	  return Pattern.compile(keyword.replace(PLACEHOLDER_REQ_LINK, REGEX_LINK));
	}
}
