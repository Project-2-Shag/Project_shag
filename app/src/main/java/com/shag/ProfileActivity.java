package com.shag;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shag.map.MapsActivity;

public class ProfileActivity extends AppCompatActivity {
    private EditText nameId, documentId;
    private Button nextButton;
    DatabaseReference ref;
    User user;
    private FirebaseAuth mAuth;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage("Отменить регистрацию?")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mAuth.getCurrentUser().delete();
                            finish();
                            return;
                        }
                    })
                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    })
                    .show();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameId = (EditText) findViewById(R.id.nameId);
        documentId = (EditText) findViewById(R.id.documentId);
        nextButton = (Button) findViewById(R.id.nextButton);
        ref = FirebaseDatabase.getInstance().getReference().child("User");
        mAuth = FirebaseAuth.getInstance();
        user = new User();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameId.getText().toString();
                String document = documentId.getText().toString();

                final String[] subName;
                String delimeter = " ";
                subName = name.split(delimeter);

                if (subName[0].isEmpty())
                {
                    nameId.setError("Пожалуйста, введите имя");
                    nameId.requestFocus();
                }
                else if (subName[1].isEmpty())
                {
                    nameId.setError("Пожалуйста, введите фамилию");
                    nameId.requestFocus();
                }
                else if (subName[2].isEmpty())
                {
                    nameId.setError("Пожалуйста, введите отчество");
                    nameId.requestFocus();
                }
                else if (document.isEmpty())
                {
                    documentId.setError("Пожалуйста, введите номер Вашего документа");
                    documentId.requestFocus();
                }
                else
                {
                    FirebaseUser firebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                    assert firebaseCurrentUser != null;
                    String email = firebaseCurrentUser.getEmail();

                    user.setName(subName[1]);
                    user.setSecondName(subName[0]);
                    user.setThirdName(subName[2]);
                    user.setDocument(document);
                    user.setEmail(email);
                    ref.push().setValue(user);

                    Intent intent = new Intent(ProfileActivity.this, MapsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
