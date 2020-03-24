package com.shag;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


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

        upToWorkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String driverId = driverIdId.getText().toString();

                // !!!!!!!!!!!!!!!!!
                // добавить чтение из базы данных
                // создать новый класс и лэйаут для домашнего экрана водителя
                // !!!!!!!!!!!!!!!!!
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
