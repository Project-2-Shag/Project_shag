package com.shag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;


public class RegistrationActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    EditText emailId, passwordId, passwordIdConfirm;
    Button registrationButton;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        emailId = (EditText) findViewById(R.id.emailId);
        passwordId = (EditText) findViewById(R.id.passwordId);
        passwordIdConfirm = (EditText) findViewById(R.id.passwordIdConfirm);
        registrationButton = (Button) findViewById(R.id.nextButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progressBar.setVisibility(ProgressBar.VISIBLE);
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
                    //progressBar.setVisibility(ProgressBar.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, password).
                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        Intent intent = new Intent(RegistrationActivity.this, ProfileActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        String exceptionCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                                        switch (exceptionCode)
                                        {

                                            case "ERROR_INVALID_EMAIL":
                                                emailId.setError("Пожалуйста, введите корректную электронную почту");
                                                emailId.requestFocus();
                                                break;

                                            case "ERROR_EMAIL_ALREADY_IN_USE":
                                                emailId.setError("Аккаунт с данной элекронной почтой уже существует");
                                                emailId.requestFocus();
                                                break;

                                            case "ERROR_WEAK_PASSWORD":
                                                passwordId.setError("Пароль должен содержать минимум 6 символов");
                                                passwordId.requestFocus();
                                                break;
                                        }
                                    }
                                }
                            });
                    //progressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        });
    }
}