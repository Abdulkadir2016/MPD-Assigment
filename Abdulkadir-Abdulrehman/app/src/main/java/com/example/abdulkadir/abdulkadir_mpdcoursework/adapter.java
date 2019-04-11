package com.example.abdulkadir.abdulkadir_mpdcoursework;



//StudentName:Abdulkadir Abdulrehman
//        StudentID           S1712579
//        MPD: Coursework
//        program of study:computing

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;




public class adapter extends ArrayAdapter<ListItemObject> {
    ListItemObject singleitem;

    //constructor
    public adapter(Context context, ArrayList<ListItemObject> item) {
        super(context, R.layout.list_view, item);
    }

    @Override
    public View getView(int position, View view,ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View List_view = inflater.inflate(R.layout.list_view,parent,false);

        //get items lists
        singleitem = getItem(position);

        //get element
        TextView line1 = (TextView) List_view.findViewById(R.id.line1);
        TextView line2 = (TextView) List_view.findViewById(R.id.line2);
        TextView line3 = (TextView) List_view.findViewById(R.id.line3);
        TextView line4 = (TextView) List_view.findViewById(R.id.line4);

        //set for each element
        line1.setText(singleitem.getTitle());
        line2.setText(singleitem.getDate());
        line3.setText("Magnitude: "+ String.valueOf(singleitem.getMagnitude()));
        line4.setText("Depth: "+ String.valueOf(singleitem.getDepth())+" km");

        return List_view;
    }

}

