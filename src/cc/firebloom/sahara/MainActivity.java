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
}
