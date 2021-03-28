package com.example.diplom1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddEating extends AppCompatActivity {
    TextView name, calories,fats,carbohydrates,proteins;
    EditText countGrams;
    ImageView image;
    FloatingActionButton saveButton;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_eating);

        name = findViewById(R.id.addName);
        calories = findViewById(R.id.calories);
        fats = findViewById(R.id.fats);
        carbohydrates = findViewById(R.id.carbohydrates);
        proteins = findViewById(R.id.proteins);
        countGrams = findViewById(R.id.addCount);
        image = findViewById(R.id.addImage);
        saveButton = findViewById(R.id.saveEating);
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        //add values in imageView, and textViews
        addValues();

        //click on fab
        saveEating();;

    }

    private void addValues(){
        //connectToFirebase
        databaseReference = FirebaseDatabase.getInstance().getReference().child("eatings");
        String eating = getIntent().getStringExtra("Eating");

        databaseReference.child(eating).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //getValues
                if (snapshot.exists()){
                    String nameEating = snapshot.child("name").getValue().toString();
                    String imageEating = snapshot.child("image").getValue().toString();
                    String caloriesEating = snapshot.child("calories").getValue().toString();
                    String carbohydratesEating = snapshot.child("carbohydrates").getValue().toString();
                    String fatsEating = snapshot.child("fats").getValue().toString();
                    String proteinsEating = snapshot.child("proteins").getValue().toString();
                    Glide.with(AddEating.this)
                            .load(imageEating)
                            .into(image);

                    name.setText(nameEating);
                    calories.setText(caloriesEating);
                    proteins.setText(proteinsEating);
                    fats.setText(fatsEating);
                    carbohydrates.setText(carbohydratesEating);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveEating(){
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getValues
                //name
                String nameEating = name.getText().toString();
                //grams
                int countGramsEating = Integer.parseInt(countGrams.getText().toString());
                //calories
                int caloriesEating = Integer.parseInt(calories.getText().toString());
                float allCalories = (float)countGramsEating/100 * (float) caloriesEating;
                //proteins
                int proteinsEating = Integer.parseInt(proteins.getText().toString());
                float allProteins = (float)countGramsEating/100 * (float)proteinsEating;
                //fats
                int fatsEating = Integer.parseInt(fats.getText().toString());
                float allFats = (float)countGramsEating/100 * (float)fatsEating;
                //carbohydrates
                int carbohydratesEating = Integer.parseInt(carbohydrates.getText().toString());
                float allCarbohydrates = (float)countGramsEating/100 * (float)carbohydratesEating;

                //add new collection for each user and add new Eating in this collection
                    CollectionReference collectionReference = FirebaseFirestore.getInstance()
                            .collection("users/"+userId+"/eating");
                    collectionReference.add(new Eating(nameEating,countGramsEating,allCalories,allProteins,allFats,allCarbohydrates));
                    Toast.makeText(AddEating.this,"Eating added",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddEating.this,ListOfEating.class));
                    finish();

            }
        });
    }

}