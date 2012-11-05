package cc.firebloom.sahara.sender;

import java.io.IOException;
import java.util.Collection;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.firebloom.sahara.R;

public class BlackListAdapter extends ArrayAdapter<String> {
  protected Context mContext;
  protected LayoutInflater mInflater;
  
  protected int mFullSize;
  protected int mCustomSize;
  protected int mPublicSize;
  
  public BlackListAdapter(Context context) {
    super(context, android.R.id.text1);
    
    mContext = context;
    mInflater = LayoutInflater.from(context);
    
    Sender sender = Sender.getInst(context);
    mFullSize = sender.fullList().size();
    mCustomSize = sender.customList().size();
    try {
      mPublicSize = sender.publicList().size();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    if(VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT) {
      _addAll(sender.fullList());
    } else {
      for(String num:sender.fullList()) {
        add(num);
      }
    }
  }
  
  @TargetApi(11)
  private void _addAll(Collection<String> collection){
    addAll(collection);
  }
  
  public void reload(){
    Sender sender = Sender.getInst(mContext);
    mFullSize = sender.fullList().size();
    mCustomSize = sender.customList().size();
    try {
      mPublicSize = sender.publicList().size();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    clear();
    if(VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT) {
      _addAll(sender.fullList());
    } else {
      for(String num:sender.fullList()) {
        add(num);
      }
    }
  }
  
  class ItemViewHolder{
    TextView header;
    TextView text;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ItemViewHolder holder;
    
    if (null == convertView) {
      convertView = buildItemView();
      
      holder = new ItemViewHolder();
      holder.header = (TextView) convertView.findViewById(android.R.id.title);
      holder.text = (TextView) convertView.findViewById(android.R.id.text1);
      
      convertView.setTag(holder);
    } else {
      holder = (ItemViewHolder) convertView.getTag();
    }
    
    holder.text.setText(getItem(position));
    
    String header = null;
    if (this.getCount() == mFullSize) { // filter not in use
      if (0 == position) {
        if (0 == mCustomSize) {
          header = mContext.getString(R.string.sender_black_list_section_public);
        } else {
          header = mContext.getString(R.string.sender_black_list_section_custom);
        }
      } else if (mCustomSize == position) {
        header = mContext.getString(R.string.sender_black_list_section_public);
      }
    }
    
    if (null == header) {
      holder.header.setVisibility(View.GONE);
    } else {
      holder.header.setText(header);
      holder.header.setVisibility(View.VISIBLE);
    }
    
    return convertView;
  }
  
  protected View buildItemView(){
    LinearLayout view = new LinearLayout(mContext);
    view.setOrientation(LinearLayout.VERTICAL);
    
    view.addView(mInflater.inflate(android.R.layout.preference_category, null));
    view.addView(mInflater.inflate(android.R.layout.simple_list_item_1, null));
    
    return view;
  }
  
}
