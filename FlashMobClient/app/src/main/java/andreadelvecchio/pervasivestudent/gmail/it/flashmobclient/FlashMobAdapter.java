package andreadelvecchio.pervasivestudent.gmail.it.flashmobclient;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class FlashMobAdapter extends BaseAdapter {

    private Context mContext;
    private  FlashMob[] list;


    // Store the list of image IDs
    public FlashMobAdapter(Context c, FlashMob[] ids) {
        mContext = c;
        this.list = ids;
    }

    // Return the number of items in the Adapter
    @Override
    public int getCount() {
        return list.length;
    }

    // Return the data item at position
    @Override
    public Object getItem(int position) {
        return list[position];
    }

    // Will get called to provide the ID that
    // is passed to OnItemClickListener.onItemClick()
    @Override
    public long getItemId(int position) {
        return position;
    }

    // Return an ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.adapter_layout, null);

            TextView title = (TextView) v.findViewById(R.id.title);
            TextView descriprion = (TextView) v.findViewById(R.id.description);
            TextView startDate = (TextView) v.findViewById(R.id.startDate);
            TextView endDate = (TextView) v.findViewById(R.id.endDate);

            title.setText(list[position].getName());
            descriprion.setText(list[position].getDescription());
            startDate.setText(FlashMob.DATEFORMAT.format(list[position].getStart()));
            endDate.setText(FlashMob.DATEFORMAT.format(list[position].getEnd()));


        }
        else v = convertView ;

        return v;
    }
}

