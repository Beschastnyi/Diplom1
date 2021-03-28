package com.example.diplom1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    Button buttonCreateAccount, buttonLogin, buttonForget;
    EditText email, password;
    FirebaseAuth firebaseAuth;
    AlertDialog.Builder reset_alert;
    LayoutInflater emailInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.editEmail);
        password = findViewById(R.id.editPassword);
        buttonCreateAccount = findViewById(R.id.createAccountButton);
        buttonLogin = findViewById(R.id.loginButton);
        buttonForget = findViewById(R.id.forgetButton);
        reset_alert = new AlertDialog.Builder(this);
        emailInflater = this.getLayoutInflater();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Button CreateAccount
        createAccount();

        //Button Login
        login();

        //Button forget
        forget();
    }

    private void createAccount(){
        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Registration.class));
            }
        });

    }

    private void login(){
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //2 warnings
                if(email.getText().toString().isEmpty()){
                    email.setError("Email is Missing");
                    return;
                }

                if(password.getText().toString().isEmpty()){
                    password.setError("Password is Missing");
                    return;
                }

                //Complete operation
                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            //Success
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    //Failure
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    private void forget(){
        buttonForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = emailInflater.inflate(R.layout.alert_email,null);
                reset_alert.setTitle("Reset forgot password")
                        .setMessage("Enter your email to get password reset link")
                        .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText newEmail = view.findViewById(R.id.editNewEmail);
                                if (newEmail.getText().toString().isEmpty()){
                                    newEmail.setError("New email is empty");
                                    return;
                                }

                                firebaseAuth.sendPasswordResetEmail(newEmail.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Login.this,"Reset Email sent", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Login.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }).setNegativeButton("Cancel",null)
                        .setView(view)
                        .create().show();
            }
        });

    }

    //Get current User
    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
    }
}