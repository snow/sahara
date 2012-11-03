package cc.firebloom.sahara.sender;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import cc.firebloom.sahara.R;

import com.emilsjolander.components.StickyListHeaders.StickyListHeadersListView;

public class BlackListActivity extends Activity {
  
  private ListView mListView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sender_black_list);

    if (VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT) {
      configActionBar();
    }
    
    mListView = (StickyListHeadersListView) findViewById(android.R.id.list);
    mListView.setAdapter(new BlackListAdapter(getBaseContext()));
    mListView.setFastScrollEnabled(true);
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
      // finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  public void onAdd(View view){
    String num = ((EditText) findViewById(R.id.text)).getText().toString();
    Sender.getInst(this).addNumber(num);
    ((BlackListAdapter) mListView.getAdapter()).notifyDataSetChanged();
  }
}
