package com.skripsi.adminjilbabqu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.skripsi.adminjilbabqu.auth.LoginActivity;
import com.skripsi.adminjilbabqu.auth.SessionManager;
import com.skripsi.adminjilbabqu.helper.utils.CekKoneksi;
import com.skripsi.adminjilbabqu.helper.utils.CustomProgressbar;
import com.skripsi.adminjilbabqu.menu.KategoriActivity;
import com.skripsi.adminjilbabqu.menu.LaporanActivity;
import com.skripsi.adminjilbabqu.menu.PenjualanActivity;
import com.skripsi.adminjilbabqu.menu.ProdukActivity;
import com.skripsi.adminjilbabqu.menu.produk.TambahProdukActivity;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    CekKoneksi koneksi = new CekKoneksi();
    public SessionManager SessionManager;
    public static String iduser, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SessionManager = new SessionManager(MainActivity.this);
        SessionManager.checkLogin();
        HashMap<String, String> user = SessionManager.getUserDetails();
        iduser = user.get(SessionManager.KEY_ID);
        username = user.get(SessionManager.KEY_USERNAME);

        ActionButton();
    }

    private void ActionButton() {
        findViewById(R.id.cv1).setOnClickListener(v -> {
            startActivity(new Intent(this, KategoriActivity.class));
        });
        findViewById(R.id.cv2).setOnClickListener(v -> {
            startActivity(new Intent(this, TambahProdukActivity.class));
        });
        findViewById(R.id.cv3).setOnClickListener(v -> {
            startActivity(new Intent(this, ProdukActivity.class));
        });
        findViewById(R.id.cv4).setOnClickListener(v -> {
            startActivity(new Intent(this, PenjualanActivity.class));
        });
        findViewById(R.id.cv5).setOnClickListener(v -> {
            startActivity(new Intent(this, LaporanActivity.class));
        });
        findViewById(R.id.text_logout).setOnClickListener(v -> {
            logoutUser();
        });
    }

    private void logoutUser() {
        SessionManager.logoutUser();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finishAffinity();
    }

}