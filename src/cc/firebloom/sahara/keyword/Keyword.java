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
  //protected static final String REGEX_LINK = "\\+?[\\d-\\s]{5,}|[a-zA-Z0-9-]+\\.[a-zA-Z]{2,3}\\b";
  protected static final Pattern LINK_PATTERN = 
      Pattern.compile("\\+?[\\d-\\s]{5,}|[a-z0-9-]+\\.[a-z]{2,3}\\b");
  //protected static final String REGEX_ZHCN_PUNCT = "[“！？；。，…【】《》『』]+";
  protected static final String KEYWORD_URI = "http://raw.github.com/" +
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
    // this line break url pattern
    //text = text.replaceAll("\\s+", "").replaceAll("\\p{Punct}", "").
    //    replaceAll(REGEX_ZHCN_PUNCT, "");
    text = text.toLowerCase();
    
    for(String kw:list()){
      boolean isLinkRequired = kw.contains(PLACEHOLDER_REQ_LINK);
      Pattern p;
      if (isLinkRequired) {
        p = Pattern.compile(kw.replace(PLACEHOLDER_REQ_LINK, ""));
      } else {
        p = Pattern.compile(kw);
      }
      
      Matcher m = p.matcher(text);
      if(m.find()){
        if (isLinkRequired) {
          Matcher linkMatcher = LINK_PATTERN.matcher(text);
          if (linkMatcher.find()){
            return kw;
          }
        } else {
          return kw;
        }
      }
    }
    
    return null;
  }
  
  public ArrayList<String> list() {
    if (null == _list) {
      _list = new ArrayList<String>();
    }
     
    if (0 == _list.size()) {
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
