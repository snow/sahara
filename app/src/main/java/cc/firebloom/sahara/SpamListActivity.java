package cc.firebloom.sahara;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class SpamListActivity extends ListActivity {
    private Adapter mAdapter;
    private ArrayList<String> mFiles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_spam_list);
        if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            configActionBar();
        }

        mFiles = new ArrayList<>();
        mAdapter = new Adapter();
        setListAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // TODO: toast
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) return;

        File extStorageDir = new File(Sahara.Message.STORE_PATH);
        // TODO: toast
        if (!extStorageDir.exists()) return;

        mFiles.clear();
        for (File file : extStorageDir.listFiles()) {
            mFiles.add(file.getName());
        }

        Collections.sort(mFiles, Collections.reverseOrder());

        mAdapter.notifyDataSetChanged();
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
        Intent intent = new Intent(this, SpamDetailActivity.class);
        intent.putExtra(SpamDetailActivity.EXTRA_FILE, mFiles.get(position));

        startActivity(intent);
    }

    private static class ViewHolder {
        TextView from;
        TextView time;
        TextView text;
        int position;
    }

    private class Adapter extends BaseAdapter {
        private LayoutInflater _inflater;
        private Yaml _yaml;

        Adapter() {
            _inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            _yaml = new Yaml();
        }

        @Override
        public int getCount() {
            return mFiles.size();
        }

        @Override
        public String getItem(int position) {
            return mFiles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (null == convertView) {
                convertView = _inflater.inflate(R.layout.spam_list_item, parent, false);

                holder = new ViewHolder();
                holder.from = (TextView) convertView.findViewById(R.id.from);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.position = position;

            new AsyncTask<ViewHolder, Void, Map<String, String>>() {
                private ViewHolder _v;

                @Override
                protected Map<String, String> doInBackground(ViewHolder... params) {
                    _v = params[0];

                    File file = new File(Sahara.Message.STORE_PATH, getItem(_v.position));
                    InputStream is;
                    try {
                        is = new FileInputStream(file);
                        return (Map<String, String>) _yaml.load(is);
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

                    _v.from.setText(result.get(Sahara.Message.FROM));
                    try {
                        _v.time.setText(Sahara.humanizeYamlDate(result.get(Sahara.Message
                                .SENT_AT)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    _v.text.setText(result.get(Sahara.Message.TEXT));
                }
            }.execute(holder);

            return convertView;
        }
    }
}
