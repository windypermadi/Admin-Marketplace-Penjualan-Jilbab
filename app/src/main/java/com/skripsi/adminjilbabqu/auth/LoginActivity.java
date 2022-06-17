package com.skripsi.adminjilbabqu.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.skripsi.adminjilbabqu.MainActivity;
import com.skripsi.adminjilbabqu.R;
import com.skripsi.adminjilbabqu.helper.Connection;
import com.skripsi.adminjilbabqu.helper.utils.CekKoneksi;
import com.skripsi.adminjilbabqu.helper.utils.CustomDialog;
import com.skripsi.adminjilbabqu.helper.utils.CustomProgressbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class LoginActivity extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    CekKoneksi koneksi = new CekKoneksi();

    private EditText et_username, et_pass;
    private SessionManager SessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SessionManager = new SessionManager(getApplicationContext());
        if (SessionManager.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        et_username = findViewById(R.id.et_username);
        et_pass = findViewById(R.id.et_pass);

        ActionButton();
    }

    private void ActionButton() {
        findViewById(R.id.text_login).setOnClickListener(v -> {
            if (koneksi.isConnected(LoginActivity.this)) {

                String username = et_username.getText().toString().trim();
                String pass_login = et_pass.getText().toString().trim();

                if (!username.isEmpty() && !pass_login.isEmpty()) {
                    LoginProses(username, pass_login);
                } else {
                    CustomDialog.errorDialog(LoginActivity.this, "Data tidak boleh ada yang kosong.");
                }
            } else {
                CustomDialog.noInternet(LoginActivity.this);
            }
        });
    }

    private void LoginProses(final String username, final String pass_login) {
        customProgress.showProgress(this, false);
        AndroidNetworking.post(Connection.CONNECT + "AdminLogin.php")
                .addBodyParameter("tag", "login")
                .addBodyParameter("username", username)
                .addBodyParameter("password", pass_login)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        customProgress.hideProgress();
                        clearApplicationData();
                        SessionManager.createLoginSession(
                                response.optString("iduser"),
                                response.optString("username"));

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finishAffinity();
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            customProgress.hideProgress();
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                CustomDialog.errorDialog(LoginActivity.this, body.optString("pesan"));
                            } catch (JSONException ignored) {
                            }
                        } else {
                            customProgress.hideProgress();
                            CustomDialog.errorDialog(LoginActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            int i = 0;
            assert children != null;
            while (i < children.length) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
                i++;
            }
        }

        assert dir != null;
        return dir.delete();
    }
}