package com.example.diplom1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class EatingAdapterForList extends FirestoreRecyclerAdapter<Eating, EatingAdapterForList.EatingHolder> {
    OnItemClickListener listener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public EatingAdapterForList(@NonNull FirestoreRecyclerOptions<Eating> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull EatingHolder eatingHolder, int i, @NonNull Eating eating) {
        eatingHolder.name.setText(eating.getName());
        eatingHolder.count.setText(String.valueOf(eating.getCount()));
        eatingHolder.calories.setText(String.valueOf(eating.getCalories()));

    }

    @NonNull
    @Override
    public EatingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_eating,parent,false);
        return new EatingHolder(view);
    }

    public void deleteEating(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class EatingHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView count;
        TextView calories;

        public EatingHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameEating);
            count = itemView.findViewById(R.id.countGram);
            calories = itemView.findViewById(R.id.caloriesEating);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener!=null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot,int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
