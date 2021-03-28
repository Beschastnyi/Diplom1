package com.example.diplom1;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//Holder for attributes in recycler view in ChooseEating
public class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView image;
    TextView name;
    View v;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        image = (ImageView)itemView.findViewById(R.id.chooseImage);
        name = (TextView)itemView.findViewById(R.id.chooseName);
        v = itemView;
    }
}
