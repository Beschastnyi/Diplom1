package com.example.diplom1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class ChooseEating extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<ModelChooseEating,MyViewHolder> firebaseRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_eating);

        //RecyclerView principles of working
        workAdapter();
    }


    private void workAdapter(){
        recyclerView = findViewById(R.id.recyclerViewChoose);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<ModelChooseEating> options =
                new FirebaseRecyclerOptions.Builder<ModelChooseEating>()
                        .setQuery(FirebaseDatabase.getInstance()
                                        .getReference().
                                                child("eatings")
                                , ModelChooseEating.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ModelChooseEating, MyViewHolder>(options) {
            //SetValues in our view
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull ModelChooseEating model) {
                holder.name.setText(model.getName());
                Glide.with(holder.image.getContext())
                        .load(model.getImage())
                        .into(holder.image);

                //set Listener on view
                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ChooseEating.this,AddEating.class);
                        intent.putExtra("Eating",getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }

            //Create our view
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choose_eating,parent,false);
                return new MyViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }
}