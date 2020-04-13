package com.shag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Admin2Activity extends AppCompatActivity
{
    private DatabaseReference ref;
    private List<Driver> drivers = new ArrayList<>();

    private FirebaseRecyclerOptions<Driver> option;
    private FirebaseRecyclerAdapter<Driver, DriverViewHolder> adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin2);

        ref = FirebaseDatabase.getInstance().getReference().child("Driver");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
//            {
//                drivers.clear();
//                List<String> keys = new ArrayList<>();
//                for (DataSnapshot ds : dataSnapshot.getChildren())
//                {
//                    keys.add(ds.getKey());
//                    Driver driver = ds.getValue(Driver.class);
//                    drivers.add(driver);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) { }
//        });

        option = new FirebaseRecyclerOptions.Builder<Driver>().setQuery(ref, Driver.class).build();
        adapter = new FirebaseRecyclerAdapter<Driver, DriverViewHolder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull DriverViewHolder driverViewHolder, int i, @NonNull Driver driver) {
                driverViewHolder.name.setText(driver.getSecondName() + " " + driver.getName() + " " + driver.getThirdName());
                driverViewHolder.id.setText(driver.getId());
                driverViewHolder.way.setText(driver.getWay());
            }

            @NonNull
            @Override
            public DriverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item, parent, false);
                return new DriverViewHolder(view);
            }

            @Override
            public int getItemCount() {
                return 1;
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}
