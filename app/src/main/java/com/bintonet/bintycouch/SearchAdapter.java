package com.bintonet.bintycouch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Darren on 19/08/2014.
 */
public class SearchAdapter extends SimpleAdapter {

    private Context mContext;
    public LayoutInflater inflater=null;
    public SearchAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        mContext = context;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_item_search, null);

        HashMap<String, String> data = (HashMap<String, String>) getItem(position);
        System.out.println(data);

        String url = (String) data.get("image");

        try {
            new DownloadImageTask((ImageView) vi.findViewById(R.id.list_item_icon))
                    .execute(url);
        }
        catch(Exception ee)
        {
            Log.e("Wanted Adapter - loaing image error", "Using default");
            ImageView iconView = (ImageView) vi.findViewById(R.id.list_item_icon);
            iconView.setImageResource(R.drawable.ic_launcher);
        }

        TextView tvtitle = (TextView)vi.findViewById(R.id.list_item_title_textview);
//        ImageView iconView = (ImageView) vi.findViewById(R.id.list_item_icon);
        String name = (String) data.get("title");
        tvtitle.setText(name);
        return vi;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                mIcon11 = Bitmap.createScaledBitmap(mIcon11, 160, 240, false);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
//                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }

    }
}