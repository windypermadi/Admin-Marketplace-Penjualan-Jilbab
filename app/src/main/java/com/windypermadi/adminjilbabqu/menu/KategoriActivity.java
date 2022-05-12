package com.windypermadi.adminjilbabqu.menu;

import static com.windypermadi.adminjilbabqu.helper.utils.CustomProgressbar.customProgress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.windypermadi.adminjilbabqu.R;
import com.windypermadi.adminjilbabqu.helper.Connection;
import com.windypermadi.adminjilbabqu.helper.utils.CustomDialog;
import com.windypermadi.adminjilbabqu.model.KategoriModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class KategoriActivity extends AppCompatActivity {
    private RecyclerView rv_data;
    List<KategoriModel> KategoriModel;

    private EditText et_nama;
    private TextView text_simpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategori);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        et_nama = findViewById(R.id.et_nama);
        rv_data = findViewById(R.id.rv_data);
        text_simpan = findViewById(R.id.text_simpan);

        KategoriModel = new ArrayList<>();
        LinearLayoutManager x = new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false);
        rv_data.setHasFixedSize(true);
        rv_data.setLayoutManager(x);
        rv_data.setNestedScrollingEnabled(true);

        text_simpan.setOnClickListener(v -> {
            TambahData();
        });
    }

    private void TambahData() {
        AndroidNetworking.get(Connection.CONNECT + "AdminKategori.php")
                .addQueryParameter("username", username)
                .addQueryParameter("email", email)
                .addQueryParameter("password", password)
                .addQueryParameter("alamat", alamat)
                .addQueryParameter("telp", telp)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        CustomDialog.successDialog(KategoriActivity.this, response.optString("pesan"));
                        finish();
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                CustomDialog.errorDialog(KategoriActivity.this, body.optString("pesan"));
                            } catch (JSONException ignored) {
                            }
                        } else {
                            CustomDialog.errorDialog(KategoriActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        KategoriModel.clear();
        LoadData();
        super.onResume();
    }

    private void LoadData() {
        customProgress.showProgress(this, false);
        AndroidNetworking.post(Connection.CONNECT + "AdminKategori.php")
                .addBodyParameter("tag", "list")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject responses = response.getJSONObject(i);
                                KategoriModel bk = new KategoriModel(
                                        responses.getString("idkategori"),
                                        responses.getString("nama_kategori"));
                                KategoriModel.add(bk);
                            }

                            KategoriAdapter adapter = new KategoriAdapter(getApplicationContext(), KategoriModel);
                            rv_data.setAdapter(adapter);
                            customProgress.hideProgress();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            customProgress.hideProgress();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                String kode = body.optString("kode");
                                if (kode.equals("0")) {
                                    //tidak ada data
                                    CustomDialog.errorDialog(KategoriActivity.this, body.optString("pesan"));
                                } else if (kode.equals("1")) {
                                    CustomDialog.errorDialog(KategoriActivity.this, body.optString("pesan"));
                                } else {
                                    CustomDialog.errorDialog(KategoriActivity.this, body.optString("pesan"));
                                }
                            } catch (JSONException ignored) {
                            }
                        } else {
                            CustomDialog.errorDialog(KategoriActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.ProductViewHolder> {
        private final Context mCtx;
        private final List<KategoriModel> KategoriModel;

        KategoriAdapter(Context mCtx, List<KategoriModel> KategoriModel) {
            this.mCtx = mCtx;
            this.KategoriModel = KategoriModel;
        }

        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.model_list_kategori, null);
            return new ProductViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ProductViewHolder holder, int i) {
            final KategoriModel pegawai = KategoriModel.get(i);
            holder.text_nama.setText(pegawai.getNama_kategori());
        }

        @Override
        public int getItemCount() {
            return KategoriModel.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView text_nama;
            CardView cv;

            ProductViewHolder(View itemView) {
                super(itemView);
                text_nama = itemView.findViewById(R.id.text_nama);
                cv = itemView.findViewById(R.id.cv);
            }
        }
    }
}