package cc.firebloom.sahara;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class SpamListActivity extends ListActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //setContentView(R.layout.activity_spam_list);
    if (VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT){
      configActionBar();
    }

    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
      File extStorageDir = new File(Environment.getExternalStorageDirectory()
          .getPath() + "/cc.firebloom.sahara/msg_bak");
      if (extStorageDir.exists()) {
        List<String> filenames = new ArrayList<String>();
        Map<String, File> name2file = new HashMap<String, File>();
        for(File file:extStorageDir.listFiles()){
          String filename = file.getName();
          filenames.add(filename);
          name2file.put(filename, file);
        }
        Collections.sort(filenames, Collections.reverseOrder());
        SpamArrayAdapter adapter = new SpamArrayAdapter(this, filenames, name2file);
        setListAdapter(adapter);
      }
    }
  }
  
  @TargetApi(11)
  protected void configActionBar() {
    getActionBar().setDisplayHomeAsUpEnabled(true);
  }

//  @Override
//  public boolean onCreateOptionsMenu(Menu menu) {
//    getMenuInflater().inflate(R.menu.activity_spam_list, menu);
//    return true;
//  }
//
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case android.R.id.home:
      NavUtils.navigateUpFromSameTask(this);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

}
