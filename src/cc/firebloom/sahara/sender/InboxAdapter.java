package cc.firebloom.sahara.sender;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class InboxAdapter extends ArrayAdapter<String> {

  public InboxAdapter(Context context) {
    super(context, android.R.layout.simple_list_item_1, android.R.id.text1);
  }
  
  @Override
  public View getView(int position, View convertView, ViewGroup parent){
    TextView v = (TextView) super.getView(position, convertView, parent);
    
    v.setMaxLines(5);
    v.setPadding(12, 12, 12, 12);
    v.setEllipsize(TextUtils.TruncateAt.END);
    
    return v;
  }
}
