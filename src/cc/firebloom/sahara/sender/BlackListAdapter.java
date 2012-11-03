package cc.firebloom.sahara.sender;

import java.util.ArrayList;

import ru.camino.parts.adapter.SectionListAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BlackListAdapter extends SectionListAdapter {
  protected final int mHeaderLayoutId = android.R.layout.preference_category;
  protected final int mTitleTextViewId = android.R.id.title;
  
  protected SectionDetector mSectionDetector;
  
  protected Context mContext;
  protected Sender mSender;
  protected ArrayList<String> mCustomList;
  protected ArrayList<String> mPublicList;
  
  public BlackListAdapter(BaseAdapter adapter, Context context) {
    super(adapter);
    
    setSectionDetector(new MySectionDetector());
    
    mContext = context;
    mSender = new Sender(context);
  }

  

  @Override
  protected Object getSectionHeader(Object firstItem, Object secondItem) {
    if (getSectionDetector() != null) {
      return getSectionDetector().detectSection(firstItem, secondItem);
    } else {
      return null;
    }
  }

  @Override
  protected View getSectionView(Object header, View convertView, ViewGroup parent) {
    View v;
    if (convertView != null) {
      v = convertView;
    } else {
      v = View.inflate(mContext, mHeaderLayoutId, null);
    }
    ((TextView) v.findViewById(mTitleTextViewId)).setText(header.toString());

    return v;
  }
  
  protected class MySectionDetector implements SectionDetector {
    public Object detectSection(Object firstItem, Object secondItem) {
      if(null == firstItem) {
        if(mCustomList.contains(secondItem)) {
          return "custom";
        } else {
          return "public";
        }
      } else if(mCustomList.contains(firstItem) && mPublicList.contains(secondItem)) {
        return "public";
      } else if(mPublicList.contains(firstItem) && mCustomList.contains(secondItem)) {
        return "custom";
      } else {
        return null;
      }
    }
  }
  
//  protected SectionDetector sectionDetector(){
//    if(null == mSectionDetector) {
//      mSectionDetector = new SectionDetector() {
//        public Object detectSection(Object firstItem, Object secondItem) {
//          if(null == firstItem) {
//            if(mCustomList.contains(secondItem)) {
//              return "custom";
//            } else {
//              return "public";
//            }
//          } else if(mCustomList.contains(firstItem) && mPublicList.contains(secondItem)) {
//            return "public";
//          } else if(mPublicList.contains(firstItem) && mCustomList.contains(secondItem)) {
//            return "custom";
//          } else {
//            return null;
//          }
//        }
//      };
//    }
//    
//    return mSectionDetector;
//  }

}
