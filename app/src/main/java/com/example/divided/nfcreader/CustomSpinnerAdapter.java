package com.example.divided.nfcreader;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter {

    private Context context;
    private List<String> itemList;
    private String label;

    public CustomSpinnerAdapter(Context context, int textViewResourceId, List<String> itemList, String label) {

        super(context, textViewResourceId);
        this.context = context;
        this.itemList = itemList;
        this.label = label;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.setting_spinner, parent, false);
        TextView label = row.findViewById(R.id.tVStaticSmallLabel);
        Typeface labelTypeface = Typeface.createFromAsset(context.getAssets(),
                "fonts/product_sans_bold");
        label.setTypeface(labelTypeface);
        label.setText(this.label);
        TextView make = (TextView) row.findViewById(R.id.tVMainText);
        Typeface rowsTypeFace = Typeface.createFromAsset(context.getAssets(),
                "fonts/product_sans_regular");
        make.setTypeface(rowsTypeFace);
        make.setText(itemList.get(position));
        return row;
    }


    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        TextView make = (TextView) row.findViewById(android.R.id.text1);
        Typeface rowsTypeFace = Typeface.createFromAsset(context.getAssets(),
                "fonts/product_sans_regular");
        make.setTypeface(rowsTypeFace);
        make.setText(itemList.get(position));
        return row;
    }
}