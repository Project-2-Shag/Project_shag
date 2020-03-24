package com.shag;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity
{
    private EditText nameId, documentId;
    private Button nextButton;
    DatabaseReference ref;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameId = (EditText) findViewById(R.id.nameId);
        documentId = (EditText) findViewById(R.id.documentId);
        nextButton = (Button) findViewById(R.id.nextButton);
        ref = FirebaseDatabase.getInstance().getReference().child("User");
        user = new User();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //!!!!!!!!!!!!!!!!!!!!
                // пофиксить возможность обойти введение данных кнопкой "назад"
                //!!!!!!!!!!!!!!!!!!!

                String name = nameId.getText().toString();
                String document = documentId.getText().toString();

                if (name.isEmpty())
                {
                    nameId.setError("Пожалуйста, введите ФИО");
                    nameId.requestFocus();
                }
                else if (document.isEmpty())
                {
                    documentId.setError("Пожалуйста, введите номер Вашего документа");
                    documentId.requestFocus();
                }
                else
                {
                    final String[] subName;
                    String delimeter = " ";
                    subName = name.split(delimeter);
                    FirebaseUser firebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                    assert firebaseCurrentUser != null;
                    String email = firebaseCurrentUser.getEmail();

                    user.setName(subName[0]);
                    user.setSecondName(subName[1]);
                    user.setThirdName(subName[2]);
                    user.setDocument(document);
                    user.setEmail(email);
                    ref.push().setValue(user);

                    Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
