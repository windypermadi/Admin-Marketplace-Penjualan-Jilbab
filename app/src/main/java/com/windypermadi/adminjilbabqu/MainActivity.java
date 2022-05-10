package com.windypermadi.adminjilbabqu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.windypermadi.adminjilbabqu.auth.LoginActivity;
import com.windypermadi.adminjilbabqu.menu.KategoriActivity;
import com.windypermadi.adminjilbabqu.menu.LaporanActivity;
import com.windypermadi.adminjilbabqu.menu.PenjualanActivity;
import com.windypermadi.adminjilbabqu.menu.ProdukActivity;
import com.windypermadi.adminjilbabqu.menu.produk.TambahProdukActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionButton();
    }

    private void ActionButton() {
        findViewById(R.id.cv1).setOnClickListener(v -> {
            startActivity(new Intent(this, KategoriActivity.class));
            finish();
        });
        findViewById(R.id.cv2).setOnClickListener(v -> {
            startActivity(new Intent(this, TambahProdukActivity.class));
            finish();
        });
        findViewById(R.id.cv3).setOnClickListener(v -> {
            startActivity(new Intent(this, ProdukActivity.class));
            finish();
        });
        findViewById(R.id.cv4).setOnClickListener(v -> {
            startActivity(new Intent(this, PenjualanActivity.class));
            finish();
        });
        findViewById(R.id.cv5).setOnClickListener(v -> {
            startActivity(new Intent(this, LaporanActivity.class));
            finish();
        });
        findViewById(R.id.text_logout).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

}