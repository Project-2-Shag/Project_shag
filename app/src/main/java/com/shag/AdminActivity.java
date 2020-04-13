package com.shag;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


public class AdminActivity extends AppCompatActivity {

    static String symbols = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz0123456789";
    private static Random random = new Random();

    public static String generateId()
    {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 14; i++) {
            if (i == 4 || i == 9)
            {
                sb.append("-");
                continue;
            }
            sb.append(symbols.charAt(random.nextInt(symbols.length())));
        }

        return sb.toString();
    }


    EditText driverIdId, nameId, wayId;
    Button genCpoyButton, addButton, deleteButton, nextPageButton;

    ClipboardManager clipboardManager;
    ClipData clipData;

    DatabaseReference ref;
    Driver driver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        driverIdId = (EditText) findViewById(R.id.driverIdId);
        nameId = (EditText) findViewById(R.id.nameId);
        wayId = (EditText) findViewById(R.id.wayId);
        genCpoyButton = (Button) findViewById(R.id.genCpoyButton);
        addButton = (Button) findViewById(R.id.addButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        nextPageButton = (Button) findViewById(R.id.nextPageButton);

        ref = FirebaseDatabase.getInstance().getReference().child("Driver");
        driver = new Driver();

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        genCpoyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverIdId.setText(generateId());
                clipData = ClipData.newPlainText("label", driverIdId.getText());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(AdminActivity.this, "Id сгенерирован и скопирован", Toast.LENGTH_SHORT).show();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String driverId = driverIdId.getText().toString();
                String name = nameId.getText().toString();
                String way = wayId.getText().toString();

                final String[] subName;
                String delimeter = " ";
                subName = name.split(delimeter);

                if (driverId.isEmpty())
                {
                    driverIdId.setError("Сгенерируйте идентификатор");
                    driverIdId.requestFocus();
                }
                else if (subName[0].isEmpty())
                {
                    nameId.setError("Введите имя");
                    nameId.requestFocus();
                }
                else if (subName[1].isEmpty())
                {
                    nameId.setError("Введите фамилию");
                    nameId.requestFocus();
                }
                else if (subName[2].isEmpty())
                {
                    nameId.setError("Введите отчество");
                    nameId.requestFocus();
                }
                else if (way.isEmpty())
                {
                    wayId.setError("Введите номер маршрута");
                    wayId.requestFocus();
                }
                else
                {
                    Date currentDate = new Date();
                    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    String dateText = dateFormat.format(currentDate);
                    DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                    String timeText = timeFormat.format(currentDate);
                    String dateTime = dateText + "; " + timeText;

                    driver.setId(driverId);
                    driver.setName(subName[1]);
                    driver.setSecondName(subName[0]);
                    driver.setThirdName(subName[2]);
                    driver.setWay(way);
                    driver.setDateOfCreating(dateTime);
                    ref.push().setValue(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(AdminActivity.this, "Водитель добавлен", Toast.LENGTH_LONG).show();
                                driverIdId.setText("");
                                nameId.setText("");
                                wayId.setText("");
                            }
                            else
                            {
                                Toast.makeText(AdminActivity.this, "Не удалось доавбить водителя", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String driverId = driverIdId.getText().toString();
                if (driverId.isEmpty())
                {
                    driverIdId.setError("Внесите идентификатор водителя, которого хотите удалить");
                    driverIdId.requestFocus();
                }
                else
                {
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren())
                            {
                                String DriverIdToDelete = ds.child("id").getValue().toString();
                                if (driverId.equals(DriverIdToDelete))
                                {
                                    DatabaseReference deleteDriver = FirebaseDatabase.getInstance().getReference().child("Driver").child(ds.getKey());
                                    deleteDriver.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(AdminActivity.this, "Водитель удален из списка", Toast.LENGTH_LONG).show();
                                                driverIdId.setText("");
                                            }
                                            else
                                            {
                                                Toast.makeText(AdminActivity.this, "Не удалось удалить водтеля", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                }
            }

        });


        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, Admin2Activity.class);
                startActivity(intent);
            }
        });
    }
}
