package com.shag;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class AdminActivity extends AppCompatActivity
{
    public static String generateId()
    {
        int item;
        StringBuilder Id = new StringBuilder();
        for (int i = 0; i < 10; i++)
        {
            int type = (int) (Math.random() * 3);
            switch (type)
            {
                case 0:
                    // генерация числа
                    item = (int) (Math.random() * 10);
                    Id.append((char) (item));
                    break;
                case 1:
                    // генерация маленькой буквы
                    item = (int) (Math.random() * 27);
                    Id.append((char) (item + 65));
                    break;
                case 2:
                    // генерация большой буквы
                    item = (int) (Math.random() * 27);
                    Id.append((char) (item + 97));
                    break;
            }
        }
        return Id.toString();
    }

    EditText driverIdId, nameId, wayId;
    Button gencpoyButton, addButton, deleteButton, nextPageButton;
    ClipboardManager clipboardManager;
    ClipData clipData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        driverIdId = (EditText) findViewById(R.id.driverIdId);
        nameId = (EditText) findViewById(R.id.nameId);
        wayId = (EditText) findViewById(R.id.wayId);
        gencpoyButton = (Button) findViewById(R.id.gencpoyButton);
        addButton = (Button) findViewById(R.id.addButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        nextPageButton = (Button) findViewById(R.id.nextPageButton);

        gencpoyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newId = generateId();
                driverIdId.setText(newId);
                ClipData clipData = ClipData.newPlainText("", newId);
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

                //!!!!!!!!!!!!!!!
                // проверка на пустоту полей
                // добавить занесение данных о новом водителе в базу данных
                // создать новый класс "driver" с полями:
                //     айди
                //     имя
                //     фамилия
                //     отчество
                //     маршрут
                //     * дата занесения
                //!!!!!!!!!!!!!!
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //!!!!!!!!!!!!!!!
                // удалять пользователя по введеному айди из базы данных
                //!!!!!!!!!!!!!!!!
            }
        });

        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //!!!!!!!!!!!!!!!!!
                // добавить новый класс и лэйаут
                //!!!!!!!!!!!!!!!!!!
            }
        });
    }
}
