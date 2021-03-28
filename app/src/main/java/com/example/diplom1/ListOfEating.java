package com.example.diplom1;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ListOfEating extends AppCompatActivity{
    //Activity
    DrawerLayout drawerLayout;
    MaterialToolbar materialToolbar;
    NavigationView navigationView;
    View inNavView;
    TextView nameInNav, emailInNav;
    ImageView profileImageInNav;
    AlertDialog.Builder reset_alert;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    EatingAdapterForList eatingAdapterForList;
    //Firebase
    FirebaseAuth firebaseAuth;
    StorageReference storageReference;
    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;
    CollectionReference collectionReference;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_eating);

        //Work with Activity
        drawerLayout = findViewById(R.id.drawerLayoutList);
        materialToolbar = findViewById(R.id.topAppBarList);
        navigationView = findViewById(R.id.modalNavigationList);
        navigationView.setCheckedItem(R.id.listOfEating);
        reset_alert = new AlertDialog.Builder(this);
        inNavView = navigationView.getHeaderView(0);
        nameInNav = (TextView)inNavView.findViewById(R.id.fullnameHeader);
        emailInNav = (TextView)inNavView.findViewById(R.id.emailHeader);
        profileImageInNav = (ImageView) inNavView.findViewById(R.id.profileImageNavigation);
        floatingActionButton = findViewById(R.id.fab);
        //Work with Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        collectionReference = firestore.collection("users/"+userId+"/eating");


        //Click on fab
        fab();

        //Navigation principles of working
        navigation();

        //RecyclerView principles of working
        workAdapter();

        //Download small image in Navigation header;
        downloadImage();

        //setValues in name and email in navigation header
        setValues();

    }

    private void workAdapter(){
        //Create a query for recyclerView;
        Query query = collectionReference.orderBy("count",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Eating> options = new FirestoreRecyclerOptions.Builder<Eating>()
                .setQuery(query,Eating.class)
                .build();

        recyclerView = findViewById(R.id.recyclerViewList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eatingAdapterForList = new EatingAdapterForList(options);


        //add options for our adapter and set adapter in recyclerView
        eatingAdapterForList.startListening();
        recyclerView.setAdapter(eatingAdapterForList);

        //delete eating from recyclerView
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //If you press OK
                reset_alert.setTitle("Delete this Eating")
                        .setMessage("Are you sure?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                eatingAdapterForList.deleteEating(viewHolder.getAdapterPosition());
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    //if you press Cancel
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recyclerView.setAdapter(eatingAdapterForList);
                    }
                }).create().show();
            }
        }).attachToRecyclerView(recyclerView);

        eatingAdapterForList.setOnItemClickListener(new EatingAdapterForList.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Eating eating = documentSnapshot.toObject(Eating.class);
                String id = documentSnapshot.getId();
                Toast.makeText(ListOfEating.this,
                        "In: " + (position+1) + " Eating:\n" + eating.getProteins() + " proteins\n" + eating.getFats() + " fats\n" + eating.getCarbohydrates() + " carbohydrates",
                        Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void fab(){
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ChooseEating.class));
                finish();
            }
        });
    }

    private void downloadImage(){
        StorageReference profileRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                //in Drawer
                Glide.with(ListOfEating.this)
                        .load(uri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profileImageInNav);
            }
        });
    }

    private void setValues(){
        DocumentReference documentReference = firestore.collection("users").document(userId);
        documentReference.addSnapshotListener(ListOfEating.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                nameInNav.setText(value.getString("fName"));
                emailInNav.setText(value.getString("Email"));

            }
        });
    }

    private void navigation(){
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext()
                                ,MainActivity.class));
                        return true;
                    case R.id.listOfEating:
                        return true;
                    case R.id.resetPasswordInNav:
                        resetPassword();
                        return true;
                    case R.id.verifyInNav:
                        verify();
                        return true;
                    case R.id.deleteInNav:
                        deleteAccount();
                        return true;
                    case R.id.logoutInNav:
                        logout();
                        return true;
                }
                return false;
            }
        });
    }

    //last 4  buttons in navigation
    private void deleteAccount(){
        reset_alert.setTitle("Delete Account")
                .setMessage("Are you sure?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        firebaseUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ListOfEating.this,"Account deleted",Toast.LENGTH_SHORT).show();
                                firebaseAuth.signOut();
                                startActivity(new Intent(getApplicationContext(),Login.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ListOfEating.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("Cancel",null)
                .create().show();
    }

    private void resetPassword(){
        startActivity(new Intent(getApplicationContext(),ResetPassword.class));
        finish();
    }

    private void logout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));

    }

    private void verify(){
        if (!firebaseAuth.getCurrentUser().isEmailVerified()){
            firebaseAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ListOfEating.this,"Verification E-mail sent",Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
            Toast.makeText(ListOfEating.this,"You are verified",Toast.LENGTH_SHORT).show();

    }
    //**********************************

    @Override
    protected void onStart() {
        super.onStart();
        eatingAdapterForList.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        eatingAdapterForList.stopListening();
    }

}