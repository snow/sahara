package cc.firebloom.sahara.sender;

import java.io.IOException;
import java.util.ArrayList;
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

public class BlackListAdapter extends ArrayAdapter<String> {
//  protected static final int HEADER_LAYOUT = android.R.layout.preference_category;
//  protected static final int mTitleTextViewId = android.R.id.title;
  
//  protected SectionDetector mSectionDetector;
  
  protected Context mContext;
  protected LayoutInflater mInflater;
  
  protected ArrayList<String> mFullList;
  protected ArrayList<String> mCustomList;
  protected ArrayList<String> mPublicList;
  
  public BlackListAdapter(Context context) {
    super(context, android.R.id.text1);
    
    mContext = context;
    mInflater = LayoutInflater.from(context);
    
    Sender sender = Sender.getInst(context);
    mFullList = sender.fullList();
    mCustomList = sender.customList();
    try {
      mPublicList = sender.publicList();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    if(VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT) {
      _addAll(mFullList);
    } else {
      for(String num:mFullList) {
        add(num);
      }
    }
  }
  
  @TargetApi(11)
  private void _addAll(Collection<String> collection){
    addAll(collection);
  }
  
//  @Override
//  public int getCount() {
//    return mCustomList.size() + mPublicList.size();
//  }
//
//  @Override
//  public String getItem(int position) {
//    if(mCustomList.size() > position){
//      return mCustomList.get(position);
//    } else {
//      position -= mCustomList.size();
//      return mPublicList.get(position);
//    }
//  }

//  @Override
//  public long getItemId(int position) {
//    return position;
//  }
  
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
    if (this.getCount() == mFullList.size()) { // filter not in use
      if (0 == position) {
        if (0 == mCustomList.size()) {
          header = "public";
        } else {
          header = "custom";
        }
      } else if (mCustomList.size() == position) {
        header = "public";
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
