package com.shag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Admin2Activity extends AppCompatActivity
{
    private FirebaseDatabase mDatabase;
    private DatabaseReference ref;
    private List<Driver> drivers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin2);

        mDatabase = FirebaseDatabase.getInstance();
        ref = mDatabase.getReference("Driver");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                drivers.clear();
                List<String> keys = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    keys.add(ds.getKey());
                    Driver driver = ds.getValue(Driver.class);
                    drivers.add(driver);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
