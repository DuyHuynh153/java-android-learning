package com.example.bai_3;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<String> {
    private int selectedItem = -1;

    public CustomArrayAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    public void setSelectedItem(int position) {
        selectedItem = position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        // If this item is not the selected item, reset its background color
        if (position != selectedItem) {
            view.setBackgroundColor(Color.TRANSPARENT);
        } else {
            view.setBackgroundColor(Color.BLUE); // Or any color you want for selected item
        }

        return view;
    }
}
