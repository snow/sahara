package cc.firebloom.sahara.sender;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import cc.firebloom.sahara.R;

public class BlackListActivity extends ListActivity {
  
  protected ListView mListView;
  protected EditText mTextV;
  protected BlackListAdapter mAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sender_black_list);

    if (VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT) {
      configActionBar();
    }
    
    mListView = this.getListView();
    mAdapter = new BlackListAdapter(getBaseContext());
    
    setListAdapter(mAdapter);
    mListView.setFastScrollEnabled(true);
    
//    adapter.getFilter().filter("123");
    mTextV = (EditText) findViewById(R.id.text);
    mTextV.addTextChangedListener(new TextWatcher(){
      public void afterTextChanged(Editable s) {}

      public void beforeTextChanged(CharSequence s, int start, int count,
          int after) {}

      public void onTextChanged(CharSequence s, int start, int before, int count) {
        mAdapter.getFilter().filter(s);
      }
    });
    mTextV.setOnEditorActionListener(new EditText.OnEditorActionListener(){

      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
          onAdd(null);
        }
        
        return true;
      }
    });
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
    String num = mTextV.getText().toString();
    if (0 < num.length()) {
      Sender.getInst(this).addNumber(num);
      mAdapter.reload(); // reload must place before clear filter
      mTextV.setText("");
    }
    
    InputMethodManager iptmgr = 
        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
    iptmgr.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                   InputMethodManager.HIDE_NOT_ALWAYS); 
  }
}
