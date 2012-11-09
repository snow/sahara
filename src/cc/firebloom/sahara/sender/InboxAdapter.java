package cc.firebloom.sahara.sender;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import cc.firebloom.sahara.R;

public class InboxAdapter extends SimpleAdapter {
  protected Context _context;
  
  public InboxAdapter(Context context, List<? extends Map<String, ?>> data,
      int resource, String[] from, int[] to) {
    super(context, data, resource, from, to);
    
    _context = context;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent){
    convertView = super.getView(position, convertView, parent);
    TextView blockedV = (TextView) convertView.findViewById(R.id.blocked);
    
    if(0 == blockedV.getText().length()) {
      setViewUnblocked(convertView);
    } else {
      setViewBlocked(convertView);
    }
    
    return convertView;
  }
  
  public void setViewBlocked(View v) {
    TextView numberV = (TextView) v.findViewById(R.id.title);
    TextView contentV = (TextView) v.findViewById(R.id.content);
    TextView blockedV = (TextView) v.findViewById(R.id.blocked);
    
    numberV.setTextColor(numberV.getTextColors().withAlpha(130));
    contentV.setTextColor(numberV.getTextColors().withAlpha(130));
    blockedV.setText(_context.getString(R.string.inbox_blocked));
  }
  
  public void setViewUnblocked(View v) {
    TextView numberV = (TextView) v.findViewById(R.id.title);
    TextView contentV = (TextView) v.findViewById(R.id.content);
    TextView blockedV = (TextView) v.findViewById(R.id.blocked);
    
    numberV.setTextColor(numberV.getTextColors().withAlpha(255));
    contentV.setTextColor(numberV.getTextColors().withAlpha(255));
    blockedV.setText(_context.getString(R.string.inbox_unblocked));
  }
}
