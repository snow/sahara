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

import org.yaml.snakeyaml.Yaml;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import cc.firebloom.sahara.R;
import cc.firebloom.sahara.Sahara;

public class Sender {
  protected static String CUSTOM_LIST_FILE = "custom_sender_black_list.yml";
  protected static String PUBLIC_LIST_FILE = "public_sender_black_list.yml";
  protected static String LIST_URI = "http://raw.github.com/snow/sahara/" +
  		"master/res/raw/sender_black_list.yml";
  
  protected static final String BACKUP_PATH = String.format("%s/%s", 
      Environment.getExternalStorageDirectory().getPath(), 
      Sahara.PACKAGE);
  
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
  
  public boolean shouldBlockNumber(String number) {
    number = number.replaceAll("\\D", "");
    
    for(String blockedNum:fullList()){
      if(number.endsWith(blockedNum)){
        return true;
      }
    }
    
    return false;
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
//    if (null != mFull) {
//      mFull.clear();
//      mFull.addAll(customList());
//      try {
//        mFull.addAll(publicList());
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
//    }
    
    File cache = new File(cachePath());
    if(cache.exists()) {
      cache.delete();
    }
    mFull = null;
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
        for(Object num:(ArrayList<Object>)mYaml.load(in)) {
          mCustom.add(num.toString());
        }
        if (0 == mCustom.size()) {
          throw new FileNotFoundException();
        }
      } catch (FileNotFoundException e) {
        // try to read from external storage
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
          // We can read and write the media
          File backupFile = new File(BACKUP_PATH, CUSTOM_LIST_FILE);
          try {
            InputStream in = new FileInputStream(backupFile);
            for(Object num:(ArrayList<Object>)mYaml.load(in)) {
              mCustom.add(num.toString());
            }
            // save to internal storage
            if (0 < mCustom.size()) {
              saveCustomList();
            }
          } catch (FileNotFoundException e1) {
            e1.printStackTrace();
          }
        }
      }
    }
    
    return mCustom;
  }
  
  public void addNumber(String num) {
    num = num.replaceAll("\\D", "");
    customList().add(0, num);
    
    saveCustomList();
    
    flushFullList();
  }
  
  public void removeNumber(String num) {
    num = num.replaceAll("\\D", "");
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
      String yaml = mYaml.dump(mCustom);
      fos.write(yaml.getBytes());
      fos.close();
      
      // backup to external storage
      if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
        // We can read and write the media
        File backupDir = new File(BACKUP_PATH);
        if(backupDir.exists() || backupDir.mkdirs()) {
          FileOutputStream backupFOS = 
              new FileOutputStream(new File(backupDir, CUSTOM_LIST_FILE));
          backupFOS.write(yaml.getBytes());
          backupFOS.close();
        } 
      }
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
  
  protected ArrayList<String> loadPublicList() throws FileNotFoundException {
    InputStream in = mContext.openFileInput(PUBLIC_LIST_FILE);
    ArrayList<String> list = new ArrayList<String>();
    for(Object num:(ArrayList<Object>)mYaml.load(in)) {
      list.add(num.toString());
    }
    return list;
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
