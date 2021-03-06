package com.example.foodlogger;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.label.FirebaseVisionCloudImageLabelerOptions;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView signupTV;
    final static int signupIntent_CODE = 111;
    Button loginButton;
    EditText emailET, passwordET;
    FirebaseAuth auth;
    ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(MainActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.pizza);
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bm);
        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getCloudImageLabeler();
        labeler.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                        list = new ArrayList<>();
                        for (FirebaseVisionImageLabel label : labels) {
                            String text = label.getText();
                            list.add(text);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                    }
                });
        setIds();
        signupTV.setPaintFlags(signupTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        auth = FirebaseAuth.getInstance();
        signupTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent makeAccountIntent = new Intent(MainActivity.this, signupIntent.class);
                startActivity(makeAccountIntent);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (emailET.getText().toString().equals("") || passwordET.getText().toString().equals(""))
                        Toast.makeText(MainActivity.this, "Do not enter a blank field", Toast.LENGTH_SHORT).show();
                    else {
                        auth.signInWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isCanceled() || !task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Could not log in", Toast.LENGTH_SHORT).show();
                                } else {
                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("email", emailET.getText().toString().trim());
                                        jsonObject.put("password", passwordET.getText().toString().trim());
                                        OutputStreamWriter writer = new OutputStreamWriter(openFileOutput("info.json", MODE_PRIVATE));
                                        writer.write(jsonObject.toString());
                                        writer.close();
                                    } catch (Exception e) {

                                    }
                                    finish();
                                    Intent i = new Intent(getApplicationContext(), userAccount.class);
                                    startActivity(i);
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Incorrect Email or Password.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput("info.json")));
            JSONObject jsonObject = new JSONObject(bufferedReader.readLine());
            if (!jsonObject.isNull("password") || !jsonObject.isNull("email")) {
                emailET.setText(jsonObject.get("email").toString());
                passwordET.setText(jsonObject.get("password").toString());
                loginButton.performClick();
            }
        } catch (Exception e) {

        }
    }

    public void setIds() {
        signupTV = findViewById(R.id.id_makeAccountTV);
        loginButton = findViewById(R.id.id_loginButton);
        emailET = findViewById(R.id.id_emailLogin);
        passwordET = findViewById(R.id.id_passwordLogin);
    }
}