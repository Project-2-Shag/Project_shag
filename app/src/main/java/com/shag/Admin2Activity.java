package com.shag;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Admin2Activity extends AppCompatActivity
{
    private DatabaseReference ref;
    private FirebaseDatabase database;

    private RecyclerView recyclerView;
    private List<Driver> result;
    private DriverAdapter adapter;

    ClipboardManager clipboardManager;
    ClipData clipData;

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case (0):
                removeDriver(item.getGroupId());
                break;

            case (1):
                changeDriver(item.getGroupId());
                break;

            case (2):
                copyDriverId(item.getGroupId());
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin2);

        ref = FirebaseDatabase.getInstance().getReference().child("Driver");
        result = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.driver_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        adapter = new DriverAdapter(result);
        recyclerView.setAdapter(adapter);

        updateList();
    }



    private void updateList()
    {
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                result.add(dataSnapshot.getValue(Driver.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Driver driver = dataSnapshot.getValue(Driver.class);
                int index = getItemIndex(driver);
                result.set(index, driver);
                adapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Driver driver = dataSnapshot.getValue(Driver.class);
                int index = getItemIndex(driver);
                result.remove(index);
                adapter.notifyItemRemoved(index);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int getItemIndex(Driver driver)
    {
        int index = -1;
        for (int i = 0; i < result.size(); i++)
        {
            if (result.get(i).getId().equals(driver.getId()))
            {
                index = i;
                break;
            }
        }
        return index;
    }


    private void removeDriver(int position)
    {
        ref.child(result.get(position).getId()).removeValue();
    }


    private void changeDriver(int position)
    {
        Driver driver = result.get(position);
        driver.setName("ИЗМЕНЕНИЕ");

        Map<String, Object> driverValues = driver.toMap();
        Map<String, Object> updatedDriver = new HashMap<>();

        updatedDriver.put(driver.getId(), driverValues);
        ref.updateChildren(updatedDriver);
    }


    private void copyDriverId(int position)
    {
        Driver driver = result.get(position);
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipData = ClipData.newPlainText("", driver.getId());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(Admin2Activity.this, "Идентификатор скопирован", Toast.LENGTH_SHORT).show();
    }
}
