package cc.firebloom.sahara;

import java.text.ParseException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class SpamDetailActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_spam_detail);
        
        if (VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT){
          configActionBar();
        }
        
        TextView fromV = (TextView)findViewById(R.id.from);
        TextView timeV = (TextView)findViewById(R.id.time);
        TextView matchedRuleV = (TextView)findViewById(R.id.matched_rule);
        TextView textV = (TextView)findViewById(R.id.text);
        
        Intent intent = getIntent();
        fromV.setText("from: "+ intent.getStringExtra(Sahara.Message.FROM));
        try {
          timeV.setText(Sahara.humanizeYamlDate(intent.getStringExtra(Sahara.Message.SENT_AT), false));
        } catch (ParseException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        textV.setText(intent.getStringExtra(Sahara.Message.TEXT));
        matchedRuleV.setText("caught by: " + intent.getStringExtra(Sahara.Message.MATCHED_RULE));
    }
    
    @TargetApi(11)
    protected void configActionBar() {
      getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_spam_detail, menu);
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

}
