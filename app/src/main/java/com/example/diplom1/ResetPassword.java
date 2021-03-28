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
import com.google.firebase.auth.FirebaseUser;

public class ResetPassword extends AppCompatActivity {
    EditText password, confirm;
    Button saveNewPassword;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        password = findViewById(R.id.editNewPassword);
        confirm = findViewById(R.id.confirmNewPassword);
        saveNewPassword = findViewById(R.id.saveNewPassword);

        setSaveNewPassword();
    }

    private void setSaveNewPassword(){
        //New password
        saveNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //warnings
                if(password.getText().toString().isEmpty()){
                    password.setError("Enter Password");
                    return;
                }

                if(confirm.getText().toString().isEmpty() || !password.getText().toString().equals(confirm.getText().toString())){
                    confirm.setError("Passwords don`t match");
                    return;
                }

                //Confirm operation
                firebaseUser.updatePassword(password.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    //Success
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ResetPassword.this,"Password updated",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                    //Failure
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ResetPassword.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}