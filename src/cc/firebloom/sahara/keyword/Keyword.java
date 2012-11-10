package cc.firebloom.sahara.keyword;

import java.io.FileNotFoundException;
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

public class Keyword {
  protected static final String PLACEHOLDER_REQ_LINK = "__REQ_LN__";
  protected static final String REGEX_LINK = "[\\d-]{5,13}|http://[\\w\\d.]+";
  protected static final String REGEX_ZHCN_PUNCT = "[“！？；。，…【】《》『』]+";
  protected static final String KEYWORD_URI = "https://raw.github.com/" +
  		"snow/sahara/master/res/raw/init_keywords.yml";
  protected static final String LIST_FILE = "keyword_list.yml";
  
  protected Context _context;
  protected Yaml _yaml;
  
  protected ArrayList<String> _list;
  
  private static Keyword _inst;
  
  private Keyword(Context context) {
    _context = context;
    _yaml = new Yaml();
  }
  
  static public Keyword getInst(Context context) {
    if (null == _inst) {
      _inst = new Keyword(context);
    }
    
    return _inst;
  }
  
  public String match(String text) {
    text = text.replaceAll("\\s+", "").replaceAll("\\p{Punct}", "").
        replaceAll(REGEX_ZHCN_PUNCT, "");
    
    for(String kw:list()){
      Pattern p = preprocessKeyword(kw);
      Matcher m = p.matcher(text);
      if(m.find()){
        return kw;
      }
    }
    
    return null;
  }
  
  protected Pattern preprocessKeyword(String keyword){
    return Pattern.compile(keyword.replace(PLACEHOLDER_REQ_LINK, REGEX_LINK));
  }
  
  public ArrayList<String> list() {
    if (null == _list) {
      _list = new ArrayList<String>();
      
      try {
        InputStream in = _context.openFileInput(LIST_FILE);
        
        for(Object num:(ArrayList<Object>)_yaml.load(in)) {
          _list.add(num.toString());
        }
      } catch (FileNotFoundException e) {
        InputStream resIn = 
            _context.getResources().openRawResource(R.raw.keyword_list);
        try {
          FileOutputStream fos = _context.openFileOutput(LIST_FILE, 
              Context.MODE_PRIVATE);
          byte[] buffer = new byte[1024];
          int read;
          while((read = resIn.read(buffer)) != -1){
            fos.write(buffer, 0, read);
          }
          
          fos.flush();
          fos.close();
          
          InputStream in = _context.openFileInput(LIST_FILE);
          
          for(Object num:(ArrayList<Object>)_yaml.load(in)) {
            _list.add(num.toString());
          }
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    }
    
    return _list;
  }
  
  public void updateList() throws IOException {
    ConnectivityManager connMgr = (ConnectivityManager) 
        _context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
        FileOutputStream fos = _context.openFileOutput(LIST_FILE, 
            Context.MODE_PRIVATE);

        byte[] buffer = new byte[1024];
        int read;
        while((read = is.read(buffer)) != -1){
          fos.write(buffer, 0, read);
        }
        
        fos.flush();
        fos.close();
        
        _list = null;
      } else {
        Log.i("FIXME", String.format("got status code %s while downloading " +
                                     "online keyword list", respCode));
      }
      
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }
}
