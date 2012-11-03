package cc.firebloom.sahara;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class SpamListActivity extends ListActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //setContentView(R.layout.activity_spam_list);
    if (VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT){
      configActionBar();
    }

    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
      File extStorageDir = new File(Sahara.Message.STORE_PATH);
      if (extStorageDir.exists()) {
        List<String> filenames = new ArrayList<String>();
        Map<String, File> name2file = new HashMap<String, File>();
        for(File file:extStorageDir.listFiles()){
          String filename = file.getName();
          filenames.add(filename);
          name2file.put(filename, file);
        }
        
        Collections.sort(filenames, Collections.reverseOrder());
        
        List<Map<String, String>> records = new ArrayList<Map<String, String>>();
        Yaml yaml = new Yaml();
        for(String filename:filenames) {
          File file = name2file.get(filename);
          InputStream is;
          try {
            is = new FileInputStream(file);
            Map<String, String> record = (Map<String, String>) yaml.load(is);
            records.add(record);
          } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        
        SpamArrayAdapter adapter = new SpamArrayAdapter(this, records);
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
      //finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    Map<String, String> record = (Map<String, String>) getListAdapter().getItem(position);
    Intent intent = new Intent(this, SpamDetailActivity.class);
    intent.putExtra(Sahara.Message.FROM, record.get(Sahara.Message.FROM));
    intent.putExtra(Sahara.Message.SENT_AT, record.get(Sahara.Message.SENT_AT));
    intent.putExtra(Sahara.Message.TEXT, record.get(Sahara.Message.TEXT));
    intent.putExtra(Sahara.Message.MATCHED_RULE, record.get(Sahara.Message.MATCHED_RULE));
    
    startActivity(intent);
  }

}
