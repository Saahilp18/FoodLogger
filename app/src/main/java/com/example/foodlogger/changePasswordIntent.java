package com.example.foodlogger;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class changePasswordIntent extends AppCompatActivity {
    EditText newPass1, newPass2;
    Button changePassConfirmButton;
    DatabaseReference databaseRef;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_intent);
        newPass1 = findViewById(R.id.id_newPass1);
        newPass2 = findViewById(R.id.id_newPass2);
        changePassConfirmButton = findViewById(R.id.id_changePasswordButton);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        changePassConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newPass1.getText().toString().trim().equals(newPass2.getText().toString().trim())) {
                    if (newPass1.toString().trim().length() > 6) {
                        databaseRef.child(user.getUid()).child("password").setValue(newPass1.getText().toString().trim());
                        user.updatePassword(newPass1.getText().toString().trim()).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(changePasswordIntent.this, "Could not change password.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(changePasswordIntent.this, "Password Changed!", Toast.LENGTH_SHORT).show();
                                try {
                                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput("info.json")));
                                    JSONObject jsonObject = new JSONObject(bufferedReader.readLine());
                                    jsonObject.remove("password");
                                    jsonObject.put("password", newPass1.getText().toString().trim());
                                    OutputStreamWriter writer = new OutputStreamWriter(openFileOutput("info.json", MODE_PRIVATE));
                                    writer.write(jsonObject.toString());
                                    writer.close();
                                } catch (Exception e) {

                                }

                                finish();
                            }
                        });

                    } else {
                        Toast.makeText(changePasswordIntent.this, "Password must be greater than 6 characters.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(changePasswordIntent.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    newPass2.setText("");
                }
            }
        });


    }
}
