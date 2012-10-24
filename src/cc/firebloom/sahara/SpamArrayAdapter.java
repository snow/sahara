/**
 * 
 */
package cc.firebloom.sahara;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author snowhs
 *
 */
public class SpamArrayAdapter extends ArrayAdapter<Map<String, Object>> {
  private final Context context;

  public SpamArrayAdapter(Context context, List<Map<String, Object>> records) {
    super(context, android.R.layout.two_line_list_item, records);
    this.context = context;
  }
  
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(android.R.layout.two_line_list_item, parent, false);
    
    TextView titleV = (TextView)rowView.findViewById(android.R.id.text1);
    TextView textV = (TextView)rowView.findViewById(android.R.id.text2);
    //textV.setMaxLines(2);
    textV.setSingleLine();
    textV.setEllipsize(TextUtils.TruncateAt.END);
    
    Map<String, Object> record = getItem(position);
    
    titleV.setText(record.get("from").toString());
    textV.setText(record.get("text").toString());
    
    return rowView;
  }
}
