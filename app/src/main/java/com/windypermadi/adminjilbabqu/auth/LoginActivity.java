package com.windypermadi.adminjilbabqu.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.windypermadi.adminjilbabqu.MainActivity;
import com.windypermadi.adminjilbabqu.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionButton();
    }

    private void ActionButton() {
        findViewById(R.id.text_login).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}