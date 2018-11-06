package andreadelvecchio.pervasivestudent.gmail.it.flashmobclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by andrea on 29/12/17.
 */

public class ImageViewAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<File> list;


    // Store the list of image IDs
    public ImageViewAdapter(Context c, ArrayList<File> ids) {
        mContext = c;
        this.list = ids;
    }

    // Return the number of items in the Adapter
    @Override
    public int getCount() {
        return list.size();
    }

    // Return the data item at position
    @Override
    public Object getItem(int position) {
        return list.get(position);
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

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.imageview_layout, null);

            ImageView image = (ImageView) convertView.findViewById(R.id.image);
            image.setImageURI(Uri.parse("file://" + list.get(position).getAbsolutePath()));


        }
        return convertView;
    }
}


