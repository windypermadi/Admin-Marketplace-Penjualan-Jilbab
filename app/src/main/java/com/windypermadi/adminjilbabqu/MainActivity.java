package com.windypermadi.adminjilbabqu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.windypermadi.adminjilbabqu.auth.LoginActivity;
import com.windypermadi.adminjilbabqu.auth.SessionManager;
import com.windypermadi.adminjilbabqu.helper.utils.CekKoneksi;
import com.windypermadi.adminjilbabqu.helper.utils.CustomProgressbar;
import com.windypermadi.adminjilbabqu.menu.KategoriActivity;
import com.windypermadi.adminjilbabqu.menu.LaporanActivity;
import com.windypermadi.adminjilbabqu.menu.PenjualanActivity;
import com.windypermadi.adminjilbabqu.menu.ProdukActivity;
import com.windypermadi.adminjilbabqu.menu.produk.TambahProdukActivity;

import java.util.HashMap;
import java.util.List;

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