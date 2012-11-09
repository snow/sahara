package cc.firebloom.sahara.sender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import cc.firebloom.sahara.R;
import cc.firebloom.sahara.Sahara;

public class InboxActivity extends ListActivity {
  protected int _colThreadId = 0;
  protected int _colAddress = 0;
  protected int _colSubject = 0;
  protected int _colBody = 0;
  
  public static final String NUMBER = "number";
  public static final String CONTENT = "content";
  public static final String BLOCKED = "blocked";
  
  protected InboxAdapter adapter;

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
                                          },
                                          // exclude threads from contacts
                                          "person is null", 
                                          null,null);
    
    if (null != c) {
      discoverColumnNames(c);
      
      SparseArray<ArrayList<String>> threads = new SparseArray<ArrayList<String>>(c.getCount());
      
      while(c.moveToNext()) {
//        for(int i = 0; i < c.getColumnCount(); i++) {
//          Log.i("> w <", c.getColumnName(i) + ": " + c.getString(i));
//        }
//        Log.i("> w <", "---------");
        
        int threadId = c.getInt(_colThreadId);
        
        ArrayList<String> record = threads.get(threadId);
        if (null == record) {
          record = new ArrayList<String>();
          record.add(c.getString(_colAddress));
          threads.put(threadId, record);
        }
        
        record.add(c.getString(_colBody));
      }
      
      Sender sender = Sender.getInst(this);
      ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
      for (int i = 0; i < threads.size(); i++) {
        ArrayList<String> thread = threads.valueAt(i);
        HashMap<String, String> map = new HashMap<String, String>();
        String number = thread.get(0);
        map.put(NUMBER, number);
        thread.remove(0);
        map.put(CONTENT, join(thread, "\n"));
        map.put(BLOCKED, sender.shouldBlockNumber(number) ? 
                           getString(R.string.inbox_blocked) : 
                           getString(R.string.inbox_unblocked));
        
        data.add(map);
      }
      
      adapter = new InboxAdapter(
        this, 
        data, 
        R.layout.inbox_list_item, 
        new String[]{NUMBER, CONTENT, BLOCKED}, 
        new int[]{R.id.title, R.id.content, R.id.blocked});
      
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
  
  protected void onListItemClick(ListView l, View v, int position, long id) {
    Map<String, String> record = (Map<String, String>) getListAdapter().getItem(position);
    String number = record.get(NUMBER);
    
    TextView blockedV = (TextView) v.findViewById(R.id.blocked);
    Sender sender = Sender.getInst(this);
    
    if(0 == blockedV.getText().length()) {
      sender.addNumber(number);
      
      adapter.setViewBlocked(v);
    } else {
      sender.removeNumber(number);
      
      adapter.setViewUnblocked(v);
    }
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
