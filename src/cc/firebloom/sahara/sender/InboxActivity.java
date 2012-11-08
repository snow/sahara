package cc.firebloom.sahara.sender;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import cc.firebloom.sahara.R;
import cc.firebloom.sahara.Sahara;

public class InboxActivity extends ListActivity {
  protected int _colThreadId = 0;
  protected int _colAddress = 0;
  protected int _colSubject = 0;
  protected int _colBody = 0;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_inbox);
    if (VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT) {
      configActionBar();
    }
    
    Cursor c = getContentResolver().query(Sahara.SMS.INBOX_URI, 
                                          new String[]{
                                            Sahara.SMS.THREAD_ID,
                                            Sahara.SMS.ADDRESS,
                                            Sahara.SMS.SUBJECT,
                                            Sahara.SMS.BODY
                                          },null,null,null);
    
    if (null != c) {
      SparseArray<ArrayList<String>> list = new SparseArray<ArrayList<String>>(c.getCount());
      
      discoverColumnNames(c);
      
      while(c.moveToNext()) {
        int threadId = c.getInt(_colThreadId);
        
        ArrayList<String> record = list.get(threadId);
        if (null == record) {
          record = new ArrayList<String>();
          record.add(c.getString(_colAddress));
          list.put(threadId, record);
        }
        
        record.add(c.getString(_colBody));
      }
      
      InboxAdapter adapter = new InboxAdapter(this);
      for (int i = 0; i < list.size(); i++) {
        ArrayList<String> record = list.valueAt(i);
        String summary = join(record, "\n");
        adapter.add(summary);
      }
      
      setListAdapter(adapter);
    }
  }

  @TargetApi(11)
  protected void configActionBar() {
    getActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_inbox, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case android.R.id.home:
      NavUtils.navigateUpFromSameTask(this);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  protected void discoverColumnNames(Cursor c){
    _colThreadId = c.getColumnIndex(Sahara.SMS.THREAD_ID);
    _colAddress = c.getColumnIndex(Sahara.SMS.ADDRESS);
    _colSubject = c.getColumnIndex(Sahara.SMS.SUBJECT);
    _colBody = c.getColumnIndex(Sahara.SMS.BODY);
  }
  
  protected String join(ArrayList<String> s, String glue){
    int k=s.size();
    if (k==0)
      return null;
    StringBuilder out=new StringBuilder();
    out.append(s.get(0));
    for (int x=1;x<k;++x)
      out.append(glue).append(s.get(x));
    return out.toString();
  }

}
