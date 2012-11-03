package cc.firebloom.sahara.sender;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import cc.firebloom.sahara.R;

public class Sender {
  protected static String CUSTOM_LIST_FILE = "custom_sender_black_list.yml";
  protected static String PUBLIC_LIST_FILE = "public_sender_black_list.yml";
  protected static String LIST_URI = "https://raw.github.com/snow/sahara/" +
  		"master/res/raw/sender_black_list.yml";
  
  protected Context mContext;
  protected Yaml mYaml;
  
  protected ArrayList<String> mFull;
  protected ArrayList<String> mCustom;
  protected ArrayList<String> mPublic;
  
  private static Sender sm_inst;
  
  private Sender(Context context) {
    mContext = context;
    mYaml = new Yaml();
  }
  
  static public Sender getInst(Context context) {
    if (null == sm_inst) {
      sm_inst = new Sender(context);
    }
    
    return sm_inst;
  }
  
  public ArrayList<String> fullList() {
    if (null == mFull) {
      mFull = new ArrayList<String>();
      
      try {
        for(Object number:(ArrayList<Object>)mYaml.load(cacheStream())) {
          mFull.add(number.toString());
        }      
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    return mFull;
  }
  
  protected void flushFullList() {
    if (null != mFull) {
      mFull.clear();
      mFull.addAll(customList());
      try {
        mFull.addAll(publicList());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  protected InputStream cacheStream() throws IOException {
    String cachePath = cachePath();
    File cache = new File(cachePath);
    if(!cache.exists()) {
      buildCache();
    }
    
    return new FileInputStream(cachePath);
  }
  
  protected String cachePath() {
    return mContext.getCacheDir().getPath() + "/sender_black_list.yml";
  }
  
  public void buildCache() throws IOException {
    ArrayList<String> list = new ArrayList<String>();
    list.addAll(customList());
    list.addAll(publicList());
    
    // remove dup
    LinkedHashSet<String> set = new LinkedHashSet<String>();
    set.addAll(list);
    list.clear();
    list.addAll(set);
    
    FileWriter out = new FileWriter(new File(cachePath()));
    
    mYaml.dump(list, out);
  }
  
  public ArrayList<String> customList() {
    if (null == mCustom) {
      mCustom = new ArrayList<String>();
      
      try {
        InputStream in = mContext.openFileInput(CUSTOM_LIST_FILE);
        mCustom.addAll((ArrayList<String>)mYaml.load(in));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
    
    return mCustom;
  }
  
  public void addNumber(String num) {
    num = num.replaceAll("\\D", "");
    customList().add(num);
    
    flushFullList();
  }
  
  public void removeNumber(String num) {
    if (customList().remove(num)) {
      saveCustomList();
      flushFullList();
    }
  }
  
  public void saveCustomList() {
    // remove dup
    LinkedHashSet<String> set = new LinkedHashSet<String>();
    set.addAll(mCustom);
    mCustom.clear();
    mCustom.addAll(set);
    
    try {
      FileOutputStream fos = mContext.openFileOutput(CUSTOM_LIST_FILE, 
          Context.MODE_PRIVATE);
      fos.write(mYaml.dump(mCustom).getBytes());
      fos.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public ArrayList<String> publicList() throws IOException {
    if (null == mPublic) {
      mPublic = new ArrayList<String>();
      
      try {
        mPublic.addAll(loadPublicList());
      } catch (FileNotFoundException e) {
        useLocalPublicList();
        mPublic.addAll(loadPublicList()); // try again and throw exception
      }
    }
    
    return mPublic;
  }
  
  protected List<String> loadPublicList() throws FileNotFoundException {
    InputStream in = mContext.openFileInput(PUBLIC_LIST_FILE);
    return (ArrayList<String>)mYaml.load(in);
  }
    
  public void useLocalPublicList() throws IOException {
    InputStream in = mContext.getResources().
        openRawResource(R.raw.sender_black_list);
    FileOutputStream fos = mContext.openFileOutput(PUBLIC_LIST_FILE, 
        Context.MODE_PRIVATE);
    
    byte[] buffer = new byte[1024];
    int read;
    while((read = in.read(buffer)) != -1){
      fos.write(buffer, 0, read);
    }
    
    fos.flush();
    fos.close();
  }
  
  public void updatePublicList() throws IOException {
    ConnectivityManager connMgr = (ConnectivityManager) 
        mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    if (null == networkInfo || !networkInfo.isConnected()) {
       throw new RuntimeException("no network connection");
    }
    
    try {
      URL url = new URL(LIST_URI);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setReadTimeout(10000 /* milliseconds */);
      conn.setConnectTimeout(5000 /* milliseconds */);
      conn.setRequestMethod("GET");
      conn.setDoInput(true);
      conn.connect();
      
      int respCode = conn.getResponseCode();
      if (200 == respCode) {
        InputStream is = conn.getInputStream();
        FileOutputStream fos = mContext.openFileOutput(PUBLIC_LIST_FILE, 
            Context.MODE_PRIVATE);

        byte[] buffer = new byte[1024];
        int read;
        while((read = is.read(buffer)) != -1){
          fos.write(buffer, 0, read);
        }
        
        fos.flush();
        fos.close();
        
        flushFullList();
      } else {
        Log.i("FIXME", String.format("got status code %s while downloading " +
                                     "online sender black list", respCode));
      }
      
    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
