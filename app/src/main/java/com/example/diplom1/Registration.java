package com.example.diplom1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {
    EditText editFullName, editEmail, editPassword, editConfirmPassword;
    Button registerButton, loginButton;
    String userId;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        editFullName = findViewById(R.id.editFullName);
        editEmail = findViewById(R.id.editEmailRegistration);
        editPassword = findViewById(R.id.editPasswordRegistration);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginButtonRegistration);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        //Button Register
        register();

        //Button Login
        login();
    }

    private void register(){
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fullName = editFullName.getText().toString();
                final String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();
                String confirm = editConfirmPassword.getText().toString();

                //5 warnings
                if(fullName.isEmpty()){
                    editFullName.setError("Fill Full Name");
                    return;
                }

                if(email.isEmpty()){
                    editFullName.setError("Fill Email");
                    return;
                }

                if(password.isEmpty()){
                    editFullName.setError("Fill Password");
                    return;
                }

                if(confirm.isEmpty()){
                    editFullName.setError("Fill confirm");
                    return;
                }
                if (!password.equals(confirm)){
                    editPassword.setError("Passwords don`t match");
                }

                Toast.makeText(Registration.this,"Confirm",Toast.LENGTH_SHORT).show();

                //Confirm operation
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    //Success
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        userId = firebaseAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = firestore.collection("users").document(userId);
                        Map<String,Object> user = new HashMap<>();
                        user.put("fName",fullName);
                        user.put("Email",email);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Registration.this,"User created",Toast.LENGTH_SHORT).show();
                            }
                        });
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                    //Failure
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Registration.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void login(){
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });
    }
}