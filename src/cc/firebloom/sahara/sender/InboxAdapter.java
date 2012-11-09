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
  
  public static final String NUMBER = "number";
  public static final String CONTENT = "content";
  public static final String BLOCKED = "blocked";
  
  public InboxAdapter(Context context, List<? extends Map<String, ?>> data,
      int resource, String[] from, int[] to) {
    super(context, data, resource, from, to);
    
    _context = context;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent){
    convertView = super.getView(position, convertView, parent);
    
    if(isViewBlocked(convertView)) {
      setViewBlocked(convertView);
    } else {
      setViewUnblocked(convertView);
    }
    
    return convertView;
  }
  
  public void toggleBlocked(View v, int position) {
    Map<String, String> record = (Map<String, String>) getItem(position);
    String number = record.get(NUMBER);
    Sender sender = Sender.getInst(_context);
    
    if(isViewBlocked(v)) { 
      sender.removeNumber(number);
      record.put(InboxActivity.BLOCKED, 
          _context.getString(R.string.inbox_unblocked));
      
      setViewUnblocked(v);
    } else {
      sender.addNumber(number);
      record.put(InboxActivity.BLOCKED, 
          _context.getString(R.string.inbox_blocked));
      
      setViewBlocked(v);
    }
  }
  
  protected void setViewBlocked(View v) {
    TextView numberV = (TextView) v.findViewById(R.id.title);
    TextView contentV = (TextView) v.findViewById(R.id.content);
    TextView blockedV = (TextView) v.findViewById(R.id.blocked);
    
    numberV.setTextColor(numberV.getTextColors().withAlpha(130));
    contentV.setTextColor(numberV.getTextColors().withAlpha(130));
    blockedV.setText(_context.getString(R.string.inbox_blocked));
  }
  
  protected void setViewUnblocked(View v) {
    TextView numberV = (TextView) v.findViewById(R.id.title);
    TextView contentV = (TextView) v.findViewById(R.id.content);
    TextView blockedV = (TextView) v.findViewById(R.id.blocked);
    
    numberV.setTextColor(numberV.getTextColors().withAlpha(255));
    contentV.setTextColor(numberV.getTextColors().withAlpha(255));
    blockedV.setText(_context.getString(R.string.inbox_unblocked));
  }
  
  protected boolean isViewBlocked(View v){
    TextView blockedV = (TextView) v.findViewById(R.id.blocked);
    return blockedV.getText().equals(
        _context.getString(R.string.inbox_blocked));
  }
}
