package cc.firebloom.sahara.sender;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.emilsjolander.components.StickyListHeaders.StickyListHeadersBaseAdapter;

public class BlackListAdapter extends StickyListHeadersBaseAdapter {
//  protected static final int HEADER_LAYOUT = android.R.layout.preference_category;
//  protected static final int mTitleTextViewId = android.R.id.title;
  
//  protected SectionDetector mSectionDetector;
  
  protected Context mContext;
  protected LayoutInflater mInflater;
  
  protected ArrayList<String> mCustomList;
  protected ArrayList<String> mPublicList;
  
  protected static int HEADER_ID_CUSTOM = 0;
  protected static int HEADER_ID_PUBLIC = 1;
  
  public BlackListAdapter(Context context) {
    super(context);
    
    mContext = context;
    mInflater = LayoutInflater.from(context);
    Sender sender = Sender.getInst(context);
    mCustomList = sender.customList();
    try {
      mPublicList = sender.publicList();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public int getCount() {
    return mCustomList.size() + mPublicList.size();
  }

  public Object getItem(int position) {
    if(mCustomList.size() > position){
      return mCustomList.get(position);
    } else {
      position -= mCustomList.size();
      return mPublicList.get(position);
    }
  }

  public long getItemId(int position) {
    return position;
  }
  
  @Override
  public long getHeaderId(int position) {
    if(mCustomList.size() > position){
      return HEADER_ID_CUSTOM;
    } else {
      return HEADER_ID_PUBLIC;
    }
  }
  
  public String headerTitle(long id){
    if(HEADER_ID_CUSTOM == id){
      return "custom";
    } else {
      return "public";
    }
  }

  @Override
  public View getHeaderView(int position, View convertView) {
    HeaderViewHolder holder;
    
    if(convertView == null){
      holder = new HeaderViewHolder();
      convertView = mInflater.inflate(android.R.layout.preference_category, null);
      holder.text = (TextView) convertView.findViewById(android.R.id.title);
      convertView.setTag(holder);
    }else{
      holder = (HeaderViewHolder) convertView.getTag();
    }
    
    //set header text as first char in name
    holder.text.setText(headerTitle(getHeaderId(position)));
    
    return convertView;
  }

  class HeaderViewHolder{
    TextView text;
  }

  @Override
  protected View getView(int position, View convertView) {
    ItemViewHolder holder;
    
    if(convertView == null){
      holder = new ItemViewHolder();
      convertView = mInflater.inflate(android.R.layout.simple_list_item_1, null);
      holder.text = (TextView) convertView.findViewById(android.R.id.text1);
      convertView.setTag(holder);
    }else{
      holder = (ItemViewHolder) convertView.getTag();
    }
    
    holder.text.setText(getItem(position).toString());
    
    return convertView;
  }

  class ItemViewHolder{
    TextView text;
  }
  
}
