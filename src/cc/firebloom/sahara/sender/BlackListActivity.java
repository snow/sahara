package cc.firebloom.sahara.sender;

import com.emilsjolander.components.StickyListHeaders.StickyListHeadersListView;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import cc.firebloom.sahara.R;

public class BlackListActivity extends Activity {
  
  private ListView mListView;
//  protected ArrayList<String> mCustomList;
//  protected ArrayList<String> mPublicList;
  
  protected Button mAddButton;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sender_black_list);

    if (VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT) {
      configActionBar();
    }
    
//    Sender sender = new Sender(this);
//    mCustomList = sender.customList();
//    try {
//      mPublicList = sender.publicList();
//    } catch (IOException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
    
//    List<String> list = new ArrayList<String>();
//    list.addAll(mCustomList);
//    list.addAll(mPublicList);
//    ArrayAdapter<String> baseAdapter = new ArrayAdapter<String>(this, 
//        android.R.layout.simple_list_item_1, android.R.id.text1, list);
    
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
  
//  protected SectionListAdapter sectionListAdapter() {
//    List<String> list = new ArrayList<String>();
//    list.addAll(mCustomList);
//    list.addAll(mPublicList);
//    ArrayAdapter<String> baseAdapter = new ArrayAdapter<String>(this, 
//        android.R.layout.simple_list_item_1, android.R.id.text1, list);
//    
//    return new SectionListAdapter(baseAdapter, sectionDetector()) {
//      protected final int mHeaderLayoutId = android.R.layout.preference_category;
//      protected final int mTitleTextViewId = android.R.id.title;
//      
//      @Override
//      protected View getSectionView(Object header, View convertView, ViewGroup parent) {
//        View v;
//        if (convertView != null) {
//          v = convertView;
//        } else {
//          v = View.inflate(getBaseContext(), mHeaderLayoutId, null);
//        }
//        ((TextView) v.findViewById(mTitleTextViewId)).setText(header.toString());
//
//        return v;
//      }
//      
//      @Override
//      protected Object getSectionHeader(Object firstItem, Object secondItem) {
//        if (getSectionDetector() != null) {
//          return getSectionDetector().detectSection(firstItem, secondItem);
//        } else {
//          return null;
//        }
//      }
//    };
//  }
  
  
}
