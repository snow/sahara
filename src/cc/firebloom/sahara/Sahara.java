package cc.firebloom.sahara;

import android.os.Environment;

/**
 * Contract of all content provider in this project
 * 
 * @author snow.hellsing@gmail.com
 * 
 */
public final class Sahara {
  public static final String PACKAGE = "cc.firebloom.sahara";
  public static final String AUTHORITY = PACKAGE;

  // This class cannot be instantiated
  private Sahara() {
  }
  
  public static final class Message {
    public static final String FROM = "from";
    public static final String SENT_AT = "sent_at";
    public static final String TEXT = "text";
    public static final String MATCHED_RULE = "matched_rule";
    
    public static final String STORE_DIR = "blocked_messages";
    public static final String STORE_PATH = String.format("%s/%s/%s", 
        Environment.getExternalStorageDirectory().getPath(), 
        PACKAGE, 
        STORE_DIR);
  }
  
  public static final class Yaml {
    public static final String DateTimeFormat = "yyyy-MM-dd HH:mm:ss.SSS Z";
  }
}
