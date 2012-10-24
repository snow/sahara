package cc.firebloom.sahara;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
  
  public static String humanizeYamlDate(String yamlDate) throws ParseException {
    return humanizeYamlDate(yamlDate, true);
  }
  
  public static String humanizeYamlDate(String yamlDate, boolean shortFormat) 
      throws ParseException {
    DateFormat yamlFormat = new SimpleDateFormat(Sahara.Yaml.DateTimeFormat);
    Date date = yamlFormat.parse(yamlDate);
    return humanizeDate(date, shortFormat);
  }
  
  public static String humanizeDate(Date date, boolean shortFormat){
    DateFormat dateFormt;
    if(shortFormat) {
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      Date begin_of_day = cal.getTime();
      
      if(date.before(begin_of_day)){
        dateFormt = new SimpleDateFormat("MM-dd");
      } else {
        dateFormt = new SimpleDateFormat("HH:mm");
      }
    } else {
      dateFormt = new SimpleDateFormat("MM-dd HH:mm");
    }
    
    return dateFormt.format(date);
  }
}
