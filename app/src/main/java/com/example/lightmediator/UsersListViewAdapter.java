package com.example.lightmediator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class UsersListViewAdapter extends ArrayAdapter<UsersListView> {

    // invoke the suitable constructor of the ArrayAdapter class
    public UsersListViewAdapter(@NonNull Context context, ArrayList<UsersListView> arrayList) {

        // pass the context and arrayList for the super
        // constructor of the ArrayAdapter class
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // todo change this

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.users_list_view, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        UsersListView currentUser = getItem(position);

        ImageView circleImage = currentItemView.findViewById(R.id.small_circle_color);
        assert currentUser != null;
        circleImage.setImageResource(currentUser.getCircleId());
        circleImage.setColorFilter(currentUser.getColor());

        TextView textView1 = currentItemView.findViewById(R.id.textView1);
        textView1.setText(currentUser.getText1());

        TextView textView2 = currentItemView.findViewById(R.id.textView2);
        textView2.setText(currentUser.getText2());

        return currentItemView;
    }
}
