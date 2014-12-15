package com.bintonet.bintycouch;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.bintonet.bintycouch.data.FoundMovie;

import java.util.List;

/**
 * Created by Darren on 19/08/2014.
 */
 public class SearchMovieAdapter<L> extends ArrayAdapter<FoundMovie> {
    List<FoundMovie> items;

    LayoutInflater mInflater ;

    Context context;

    int layoutResourceId;

    public SearchMovieAdapter(Context context, int layoutResourceId, List<FoundMovie> items) {

        super(context, layoutResourceId, items);

        this.layoutResourceId=layoutResourceId;

        this.items = items;

        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;



        ViewHolder holder = new ViewHolder();

        if(row==null){

            mInflater = ((Activity)context).getLayoutInflater();

            row = mInflater.inflate(layoutResourceId, parent, false);

            holder.title = (TextView) row.findViewById(R.id.list_item_title_textview);

//            holder.number = (TextView) row.findViewById(R.id.contactnum);
//
//            holder.add=(Button)row.findViewById(R.id.add);

            row.setTag(holder);

        }

        else{

            holder=(ViewHolder)row.getTag();

        }

        String title=items.get(position).getFoundMovieTitle();

//        String number=items.get(position).getContactNum();

        holder.title.setText(title);

//        holder.number.setText(number);

//        holder.add.setText("Add");

        return row;

    }



    static class ViewHolder{

        TextView title;

        TextView number;

        Button add;

    }
}
