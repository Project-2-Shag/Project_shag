package com.shag;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shag.map.DriverMapActivity;


public class IamDriverActivity extends AppCompatActivity
{
    Button upToWorkButton, getNewIdButton;
    EditText driverIdId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        upToWorkButton = (Button) findViewById(R.id.upToWorkButton);
        getNewIdButton = (Button) findViewById(R.id.getNewIdButton);
        driverIdId = (EditText) findViewById(R.id.driverIdId);

        final DatabaseReference ref;
        ref = FirebaseDatabase.getInstance().getReference().child("Driver");

        upToWorkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String driverId = driverIdId.getText().toString();

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean flag = false;
                        for (DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            String driverIdToCheck = ds.child("id").getValue().toString();
                            String way = ds.child("way").getValue().toString();
                            if (driverId.equals(driverIdToCheck))
                            {
                                Intent intent = new Intent(IamDriverActivity.this, DriverMapActivity.class);
                                intent.putExtra("way", way);
                                startActivity(intent);
                                flag = true;
                                break;
                            }
                        }
                        if (!flag)
                        {
                            Toast.makeText(IamDriverActivity.this, "Неверный идентификатор", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        getNewIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IamDriverActivity.this, GetNewDriverIdActivity.class);
                startActivity(intent);
            }
        });
    }
}
