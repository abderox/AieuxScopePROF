package com.example.testchat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.testchat.Models.EmergencyContact;
import com.example.testchat.R;

import java.util.ArrayList;

public class EmergencyAdapter extends ArrayAdapter<EmergencyContact> {

    private Context context;
    private  int resource;


    public EmergencyAdapter(@NonNull Context context, int resource, @NonNull ArrayList<EmergencyContact> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(resource,parent,false);
        TextView txtname = convertView.findViewById(R.id.namecontact);
        TextView txtphone = convertView.findViewById(R.id.numcontact);
        txtname.setText(getItem(position).getName());
        txtphone.setText(getItem(position).getNumber());
        return convertView;
    }
}
