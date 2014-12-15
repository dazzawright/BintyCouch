package com.bintonet.bintycouch;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by Darren on 12/08/2014.
 */
public class WantedAdapter extends CursorAdapter{/**
 * Cache of the children views for a forecast list item.
 */
public static class ViewHolder {
    public final ImageView iconView;
    public final TextView titleView;
    public final TextView yearView;
    public final TextView plotView;


    public ViewHolder(View view) {
        iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        titleView = (TextView) view.findViewById(R.id.list_item_title_textview);
        yearView = (TextView) view.findViewById(R.id.list_item_year_textview);
        plotView = (TextView) view.findViewById(R.id.list_item_plot_textview);
    }
}




    public WantedAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_wanted, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;

    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Use placeholder image for now
//        String posterURL = cursor.getString((WantedFragment.COL_POSTER_ORIGINAL));
//        viewHolder.iconView.setImageResource(R.drawable.ic_launcher);
//        String bd = cursor.getString(WantedFragment.COL_POSTER_ORIGINAL);
//        Log.v("Wanted Adapter", bd);
        // show The Image
//        try {
//            new DownloadImageTask((ImageView) view.findViewById(R.id.list_item_icon))
//                    .execute("https://image.tmdb.org/t/p/original/qKkFk9HELmABpcPoc1HHZGIxQ5a.jpg");
//        }
//        catch(Exception ee)
//        {
//            Log.e("Wanted Adapter - loaing image error", "Using default");
//            viewHolder.iconView.setImageResource(R.drawable.ic_launcher);
//        }

        String thumbNailFilename = cursor.getString(WantedFragment.COL_CP_ID)+"_thumb.jpg";
//        Log.v("Wanted adapter", "Loading poster "+ posterFilename);
        new LoadImageTask((ImageView) view.findViewById(R.id.list_item_icon))
                .execute("/data/data/com.bintonet.bintycouch/app_imageDir", thumbNailFilename);

//        Bitmap loadedPOster = loadFromInternalStorage(posterFilename, context);
//        if (loadedPOster != null) {
//            viewHolder.iconView.setImageBitmap(loadedPOster);
//        } else {
//            viewHolder.iconView.setImageResource(R.drawable.ic_launcher);
//        }
//        viewHolder.iconView.setImageBitmap(loadedPOster);
        // Read title from cursor
//        String posterURL = cursor.getString((WantedFragment.COL_POSTER_ORIGINAL));
        String titleString = cursor.getString(WantedFragment.COL_TITLE);
//        Log.v("Wanted Adapter - bind View", posterURL);
        viewHolder.titleView.setText(titleString);

        // Read title from cursor
        String yearString = cursor.getString(WantedFragment.COL_YEAR);
        viewHolder.yearView.setText(yearString);

        String plotString = cursor.getString(WantedFragment.COL_TAGLINE);
        viewHolder.plotView.setText(plotString);

    }

//    private Bitmap loadFromInternalStorage(String filename, Context context){
//        Bitmap thumbnail = null;
//        Log.v("getThumbnail() on internal storage", "Loading Image /data/data/com.bintonet.bintycouch/app_imageDir/"+filename);
//        try {
////            File filePath = context.getFileStreamPath("/data/data/com.bintonet.bintycouch/app_imageDir/"+filename);
//            FileInputStream fi = new FileInputStream("/data/data/com.bintonet.bintycouch/app_imageDir/"+filename);
//            thumbnail = BitmapFactory.decodeStream(fi);
//            thumbnail = Bitmap.createScaledBitmap(thumbnail, 160, 120, false);
//        } catch (Exception ex) {
//            Log.e("getThumbnail() on internal storage", ex.getMessage());
//        }
//        return thumbnail;
//    }

    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... params) {
            File f=new File(params[0], params[1]);

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
//                final int THUMBNAIL_SIZE = 80;
//                bitmap = Bitmap.createScaledBitmap(bitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
//                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//        ImageView bmImage;
//
//        public DownloadImageTask(ImageView bmImage) {
//            this.bmImage = bmImage;
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
//            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//                final int THUMBNAIL_SIZE = 80;
//                mIcon11 = Bitmap.createScaledBitmap(mIcon11, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
////                e.printStackTrace();
//            }
//            return mIcon11;
//        }
//
//        protected void onPostExecute(Bitmap result) {
//            bmImage.setImageBitmap(result);
//        }
//    }
}
