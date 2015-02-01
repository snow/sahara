package cc.firebloom.sahara;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.TextView;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Map;

public class SpamDetailActivity extends Activity {
    public static final String EXTRA_FILE = "file";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_spam_detail);

        if (VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT) {
            configActionBar();
        }

        new AsyncTask<Void, Void, Map<String, String>>() {
            @Override
            protected Map<String, String> doInBackground(Void... params) {
                Yaml yaml = new Yaml();
                File file = new File(Sahara.Message.STORE_PATH, getIntent().getStringExtra
                        (EXTRA_FILE));
                InputStream is;
                try {
                    is = new FileInputStream(file);
                    return (Map<String, String>) yaml.load(is);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Map<String, String> result) {
                super.onPostExecute(result);

                // TODO: toast
                if (null == result) return;

                TextView fromV = (TextView) findViewById(R.id.from);
                TextView timeV = (TextView) findViewById(R.id.time);
                TextView matchedRuleV = (TextView) findViewById(R.id.matched_rule);
                TextView textV = (TextView) findViewById(R.id.text);

                fromV.setText(result.get(Sahara.Message.FROM));
                try {
                    timeV.setText(Sahara.humanizeYamlDate(result.get(Sahara.Message.SENT_AT)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                matchedRuleV.setText("caught by: " + result.get(Sahara.Message.MATCHED_RULE));
                textV.setText(result.get(Sahara.Message.TEXT));
            }
        }.execute();
    }

    @TargetApi(11)
    protected void configActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_spam_detail, menu);
//        return true;
//    }


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
