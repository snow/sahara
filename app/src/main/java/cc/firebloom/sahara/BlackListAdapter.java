package cc.firebloom.sahara;

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
    protected Context mContext;
    protected LayoutInflater mInflater;

    protected int mFullSize;
    protected int mCustomSize;
    protected int mPublicSize;

    public BlackListAdapter(Context context) {
        super(context, android.R.layout.list_content);

        mContext = context;
        mInflater = LayoutInflater.from(context);

        reload();
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

    protected View buildItemView() {
        LinearLayout view = new LinearLayout(mContext);
        view.setOrientation(LinearLayout.VERTICAL);

        view.addView(mInflater.inflate(android.R.layout.preference_category, null));
        view.addView(mInflater.inflate(android.R.layout.simple_list_item_1, null));

        return view;
    }

    public void reload() {
        Sender sender = Sender.getInst();
        mCustomSize = sender.customList().size();
        mPublicSize = sender.publicList().size();
        mFullSize = mCustomSize + mPublicSize;

        clear();
        if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            addAll(sender.customList());
            addAll(sender.publicList());
        } else {
            for (String s : sender.customList()) {
                add(s);
            }
            for (String s : sender.publicList()) {
                add(s);
            }
        }
    }

    class ItemViewHolder {
        TextView header;
        TextView text;
    }
}
