package com.shag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;


public class MainActivity extends AppCompatActivity
{
    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
    }


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    EditText emailId, passwordId;
    Button signInButton, registrationButton, IamDriverButton;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        emailId = (EditText) findViewById(R.id.emailId);
        passwordId = (EditText) findViewById(R.id.passwordId);
        signInButton = (Button) findViewById(R.id.signInButton);
        registrationButton = (Button) findViewById(R.id.nextButton);
        IamDriverButton = (Button) findViewById(R.id.IamDriverButton);

        IamDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IamDriverActivity.class);
                startActivity(intent);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                final String password = passwordId.getText().toString();
                if (!hasConnection(MainActivity.this))
                {
                    Toast.makeText(MainActivity.this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
                }
                else if (email.isEmpty())
                {
                    emailId.setError("Пожалуйста, введите электронную почту");
                    emailId.requestFocus();
                }
                else if (password.isEmpty())
                {
                    passwordId.setError("Пожалуйста, введите пароль");
                    passwordId.requestFocus();
                }
                else if (email.equals("admin@andrey.com") || email.equals("marat@andrey.com"))
                {
                    // пароль: AdminAdmin
                    Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                    startActivity(intent);
                }
                else
                {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                String exceptionCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                                switch (exceptionCode)
                                {
                                    case "ERROR_WRONG_PASSWORD":
                                        passwordId.setError("Неверный пароль");
                                        passwordId.setText("");
                                        passwordId.requestFocus();
                                        break;

                                    case "ERROR_USER_NOT_FOUND":
                                        emailId.setError("Пользователь с такой электронной почтой не найден");
                                        emailId.requestFocus();
                                        break;

                                    case "ERROR_INVALID_EMAIL":
                                        emailId.setError("Электронная почта введена неверно");
                                        emailId.requestFocus();
                                        break;
                                }

                                //Toast.makeText(MainActivity.this, exceptionCode, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }
}
