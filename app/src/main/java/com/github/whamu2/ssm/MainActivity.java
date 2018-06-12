package com.github.whamu2.ssm;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.widget.TextView;

import com.github.whamu2.android.ssm.SpannableStringManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = findViewById(R.id.tv);

        SpannableString keyWordSpan = SpannableStringManager.getKeyWordSpan(
                ContextCompat.getColor(this, R.color.colorAccent),
                "SpannableString工具能满足日常基础开发",
                "SpannableString"
        );

        tv.setText(keyWordSpan);
    }
}
