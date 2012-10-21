/**
 * 
 */
package cc.firebloom.sahara;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

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
public class SpamArrayAdapter extends ArrayAdapter<String> {
  private final Context context;
  private final List<String> filenames;
  private final Map<String, File> name2file;
  private final Yaml yaml;

  public SpamArrayAdapter(Context context, List<String> filenames, Map<String, File> name2file) {
    super(context, android.R.layout.two_line_list_item, filenames);
    this.context = context;
    this.filenames = filenames;
    this.name2file = name2file;
    this.yaml = new Yaml();
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
    
    String filename = this.filenames.get(position);
    File file = this.name2file.get(filename);
    
    InputStream is;
    try {
      is = new FileInputStream(file);
      Map<String, Object> record = (Map<String, Object>) this.yaml.load(is);
      
      titleV.setText(record.get("from").toString());
      textV.setText(record.get("text").toString());
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return rowView;
  }
}
