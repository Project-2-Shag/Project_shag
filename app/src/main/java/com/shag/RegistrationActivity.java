package com.shag;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


public class RegistrationActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText emailId, passwordId, passwordIdConfirm;
    private Button registrationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        emailId = (EditText) findViewById(R.id.emailId);
        passwordId = (EditText) findViewById(R.id.passwordId);
        passwordIdConfirm = (EditText) findViewById(R.id.passwordIdConfirm);
        registrationButton = (Button) findViewById(R.id.registrationButton);

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String password = passwordId.getText().toString();
                String passwordConfirm = passwordIdConfirm.getText().toString();
                if (email.isEmpty())
                {
                    emailId.setError("Пожалуйста, введите электронную почту");
                    emailId.requestFocus();
                }
                else if (password.isEmpty())
                {
                    passwordId.setError("Пожалуйста, введите пароль");
                    passwordId.requestFocus();
                }
                else if (passwordConfirm.isEmpty())
                {
                    passwordIdConfirm.setError("Пожалуйста, подтвердите пароль");
                    passwordIdConfirm.requestFocus();
                }
                else if (!password.equals(passwordConfirm))
                {
                    passwordIdConfirm.setError("Пароли не совпадают");
                    passwordIdConfirm.requestFocus();
                }
                else
                {
                    mAuth.createUserWithEmailAndPassword(email, password);
                    Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}