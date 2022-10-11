package com.example.yourtaskapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    private EditText email;
    private EditText password;
    private TextView signup_txt;
    private Button btn_login;

    private FirebaseAuth mAuth;
    private ProgressDialog mdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() !=null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }


        mdialog = new ProgressDialog(this);

        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);
        btn_login = findViewById(R.id.login_btn);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mEmail = email.getText().toString().trim();
                String mPass = password.getText().toString().trim();

                if (TextUtils.isEmpty(mEmail)) {

                    email.setError("Required field");
                    return;

                }
                if (TextUtils.isEmpty(mPass)) {

                    password.setError("Required field");
                    return;

                }

                mdialog.setMessage("processing...");

                mAuth.signInWithEmailAndPassword(mEmail, mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(), " Log in successful", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                            mdialog.dismiss();
                        }

                        else
                       {
                           Toast.makeText(getApplicationContext(), " Log in failed,please check your credentials", Toast.LENGTH_LONG).show();
                            mdialog.dismiss();
                        }

                    }
                });


            }
        });


        signup_txt = findViewById(R.id.signup_txt);
        signup_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });
    }
}
