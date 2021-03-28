package com.example.diplom1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class MainActivity extends AppCompatActivity {
    //Attributes
    final static int OPEN_REQUEST = 1000;
    TextView name, email, nameInNav, emailInNav;
    View inNavView;
    ImageView profileImage, profileImageInNav;
    String userId;
    DrawerLayout drawerLayout;
    MaterialToolbar materialToolbar;
    NavigationView navigationView;
    LinearProgressIndicator linearProgressIndicator;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    StorageReference storageReference;
    AlertDialog.Builder reset_alert;
    FirebaseStorage firebaseStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //work with buttons and textView, images, xml
        name = findViewById(R.id.textViewName);
        email = findViewById(R.id.textViewEmail);
        profileImage = findViewById(R.id.profileImage);
        reset_alert = new AlertDialog.Builder(this);
        drawerLayout = findViewById(R.id.drawerLayoutMain);
        materialToolbar = findViewById(R.id.topAppBarMain);
        navigationView = findViewById(R.id.modalNavigationMain);
        navigationView.setCheckedItem(R.id.profile);
        inNavView = navigationView.getHeaderView(0);
        nameInNav = (TextView)inNavView.findViewById(R.id.fullnameHeader);
        emailInNav = (TextView)inNavView.findViewById(R.id.emailHeader);
        profileImageInNav = (ImageView) inNavView.findViewById(R.id.profileImageNavigation);
        linearProgressIndicator = findViewById(R.id.progress);
        linearProgressIndicator.setVisibility(View.INVISIBLE);
        //work with firebase(authentication, store, storage)
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();


        navigation();

        downloadImage();

        setValues();

        addImage();

    }

    //navigation + last 4 items
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
                        return true;
                    case R.id.listOfEating:
                        startActivity(new Intent(getApplicationContext()
                                ,ListOfEating.class));
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

    private void deleteAccount(){
        reset_alert.setTitle("Delete Account")
                .setMessage("Are you sure")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        firebaseUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this,"Account deleted",Toast.LENGTH_SHORT).show();
                                firebaseAuth.signOut();
                                startActivity(new Intent(getApplicationContext(),Login.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(MainActivity.this,"Verification E-mail sent",Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
            Toast.makeText(MainActivity.this,"You are verified",Toast.LENGTH_SHORT).show();

    }


    //Work with MainActivity(Profile)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Checking
        if(requestCode == OPEN_REQUEST){
            if (resultCode == Activity.RESULT_OK){
                Uri imgUri = data.getData();
                uploadDownloadImage(imgUri);
            }
        }
    }

    //method to upload and download from Firebase storage image
    private void uploadDownloadImage(Uri imgUri){
        //upload
        final StorageReference storageReference1 = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"profile.jpg");
        storageReference1.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(MainActivity.this,"Image download", Toast.LENGTH_SHORT).show();
                //download
                storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(MainActivity.this)
                                .load(uri)
                                .apply(RequestOptions.circleCropTransform())
                                .into(profileImage);

                        Glide.with(MainActivity.this)
                                .load(uri)
                                .apply(RequestOptions.circleCropTransform())
                                .into(profileImageInNav);
                    }
                });
            }
            //Wrong
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                linearProgressIndicator.setVisibility(View.VISIBLE);
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                linearProgressIndicator.setVisibility(View.INVISIBLE);
            }
        });
    }

    //download img in profile and in nav header
    private void downloadImage(){
        StorageReference profileRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //in MainActivity
                Glide.with(MainActivity.this)
                        .load(uri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profileImage);

                //in Drawer
                Glide.with(MainActivity.this)
                        .load(uri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profileImageInNav);
            }
        });
    }

    //button image to add imageProfile
    private void addImage(){
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery,OPEN_REQUEST);
            }
        });
    }

    //set fullname and email in Profile and in nav header
    private void setValues(){
        DocumentReference documentReference = firestore.collection("users").document(userId);
        documentReference.addSnapshotListener(MainActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                name.setText(value.getString("fName"));
                email.setText(value.getString("Email"));
                nameInNav.setText(value.getString("fName"));
                emailInNav.setText(value.getString("Email"));

            }
        });
    }

}