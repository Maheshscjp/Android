package com.example.login;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListViewAdapter extends ArrayAdapter<String> {
    private static final String TAG = "ListViewAdapter";
    int groupid;
    String[] item_list;
    String item;
    List<String> List_file;

    ArrayList<String> desc;
    Context context;
    public ListViewAdapter(Context context, int vg, int id,   List<String> List_file){
        super(context,vg, id, List_file);
        this.context=context;
        groupid=vg;
        this.List_file=List_file;

    }

     // Hold views of the ListView to improve its scrolling performance
    static class ViewHolder {
        public TextView textview;
        public Button button;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        // Inflate the list_item.xml file if convertView is null
        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(groupid, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textview= (TextView) rowView.findViewById(R.id.txt);
            if(List_file.get(position).contains("Pending")) {
                Log.d(TAG, "Status :: " + List_file.get(position));
                viewHolder.button = (Button) rowView.findViewById(R.id.bt);


/*                viewHolder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    Log.d(TAG,"Test Button Click");
                    }
                });*/
            }
            else{
                viewHolder.button = (Button) rowView.findViewById(R.id.bt);
                viewHolder.button.setVisibility(View.GONE);
            }
            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item

        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.textview.setText(List_file.get(position));
        //holder.button.(List_file.get(position));
        return rowView;
    }

}
