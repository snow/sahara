package cc.firebloom.sahara.sender;

import cc.firebloom.sahara.R;
import cc.firebloom.sahara.R.layout;
import cc.firebloom.sahara.R.menu;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.annotation.TargetApi;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

public class BlackListActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender_black_list);
        
        if (VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT){
          configActionBar();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_sender_black_list, menu);
        return true;
    }
    
    @TargetApi(11)
    protected void configActionBar() {
      getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
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
}
