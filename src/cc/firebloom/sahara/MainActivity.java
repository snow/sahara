package cc.firebloom.sahara;

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class MainActivity extends PreferenceActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    if(VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT) {
      loadFragment();
    } else {
      addPreferencesFromResource(R.xml.preferences);
    }
    
    //doEvil(getBaseContext());
  }
  
  // separate these codes into a method
  // just as ``@TargetApi(11)`` seens to can't be applied directly \
  // above `getFragmentManager()` in onCreate()
  @TargetApi(11)
  protected void loadFragment() {
    getFragmentManager().beginTransaction()
      .replace(android.R.id.content, new SettingsFragment())
      .commit();
  }
  
//  private void doEvil(Context context){
//    Log.i("> w <", KeywordFilter.getKeywords(context).toString());
//    
//    ArrayList<String> ls = new ArrayList<String>();
//    ls.add("a");
//    ls.add("a");
//    ls.add("b");
//    ls.add("c");
//    
//    HashSet<String> hs = new HashSet<String>();
//    hs.addAll(ls);
//    ls.clear();
//    ls.addAll(hs);
//    
//    Yaml yaml = new Yaml();
//    Log.i("> w <", yaml.dump(ls));
//  }
}
