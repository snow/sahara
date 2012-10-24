/**
 * 
 */
package cc.firebloom.sahara;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author snowhs
 *
 */
public class SpamArrayAdapter extends ArrayAdapter<Map<String, String>> {
  private final Context context;

  public SpamArrayAdapter(Context context, List<Map<String, String>> records) {
    super(context, R.layout.spam_list_item, records);
    this.context = context;
  }
  
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowV = inflater.inflate(R.layout.spam_list_item, parent, false);
    
    TextView fromV = (TextView)rowV.findViewById(R.id.from);
    TextView timeV = (TextView)rowV.findViewById(R.id.time);
    TextView textV = (TextView)rowV.findViewById(R.id.text);
    //textV.setMaxLines(2);
    //textV.setSingleLine();
    //textV.setEllipsize(TextUtils.TruncateAt.END);
    
    Map<String, String> record = getItem(position);
    
    fromV.setText(record.get(Sahara.Message.FROM));
    //timeV.setText(record.get(Sahara.Message.SENT_AT));
    textV.setText(record.get(Sahara.Message.TEXT));
    
    DateFormat yamlFormat = new SimpleDateFormat(Sahara.Yaml.DateTimeFormat);
    try {
      Date sent_at = yamlFormat.parse(record.get(Sahara.Message.SENT_AT));
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      Date begin_of_day = cal.getTime();
      
      DateFormat shortDateFormt;
      if(sent_at.before(begin_of_day)){
        shortDateFormt = new SimpleDateFormat("MM-dd");
      } else {
        shortDateFormt = new SimpleDateFormat("HH:mm");
      }
      timeV.setText(shortDateFormt.format(sent_at));
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return rowV;
  }
}
