package com.shag;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class GetNewDriverIdActivity extends AppCompatActivity
{
    Button copyNumberButton, php;
    TextView getNewDriverIdInfo;
    ClipboardManager clipboardManager;
    ClipData clipData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getnewdreiverid);

        copyNumberButton = (Button) findViewById(R.id.copyNumberButton);

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        copyNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clipData = ClipData.newPlainText("8 800 555 35 35", "8 800 555 35 35");
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(GetNewDriverIdActivity.this, "Номер скопирован", Toast.LENGTH_SHORT).show();
            }
        });

        php = (Button) findViewById(R.id.php);
        php.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyNumberButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        php.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getNewDriverIdInfo = (TextView) findViewById(R.id.getNewDriverIdInfo);
                                getNewDriverIdInfo.setText("PHP для даунов");
                                getNewDriverIdInfo.setTextSize(40);
                            }
                        });
                    }
                });
            }
        });
    }
}
